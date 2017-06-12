package com.surfilter.mass.tools.services;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.surfilter.mass.tools.entity.Relation;
import com.surfilter.mass.tools.orientdb.OrientDaoSingleton;
import com.surfilter.mass.tools.util.CertificationFilter;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.ParseLineUtil;
import com.surfilter.mass.tools.util.Threads;

public class OrientdbCliThread implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(OrientdbCliThread.class);
	public static final int BATCH_SIZE = 2000;

	private BlockingQueue<String> queue;
	private List<Relation> relations = new ArrayList<>(BATCH_SIZE);
	private List<String> lines = new ArrayList<>(BATCH_SIZE);
	private List<String> fileds = new ArrayList<>();
	private volatile boolean running = true;

	
	private ParquetToOrientdb orientdbCli = new ParquetToOrientdb();
	private Connection conn = null;
	private OrientDaoSingleton orientObj = null;

	// 错误数据
	private static BlockingQueue<String> errorQueue = new LinkedBlockingQueue<String>();
	// 错误数据缓冲
	private List<String> errorLines = new ArrayList<>(10000);

	private String dir;
	private String[] filters;
	private int sleep;

	public OrientdbCliThread(BlockingQueue<String> queue, String orientUrl, String dir, String filter, int sleep) {
		Preconditions.checkNotNull(queue, "queue must not be null");
		Preconditions.checkNotNull(dir, "dir must not be null");

		this.queue = queue;		
		this.dir = dir;
		this.filters = filter.split(",");
		this.sleep = sleep;
		
		this.orientObj = OrientDaoSingleton.getInstance(orientUrl);
		
	}

	@Override
	public void run() {
		try {
			this.conn = orientObj.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOG.info("orientdbObj getConnection failed : " + e);
		}
		
		while (running) {
			if (queue.isEmpty()) {
				writeDataLines();
				Threads.sleep(200);
			} else {
				queue.drainTo(lines, BATCH_SIZE);
				
				LOG.info("process................................lines size : "  + lines.size());

				if (lines.isEmpty()) {
					return;
				}

				parseLines();

				for(Relation r : relations){
					orientdbCli.addVertexEdgeNoTx(r, conn);	
				}
				
				
				relations.clear();
				
				//sleep主要是为了缓解orientdb批量入库的压力
				Threads.sleep(sleep);
			}
		}

		//writeDataLines();
		LOG.info("stop................................");
		try { if(conn != null) conn.close(); } catch (SQLException ignore) {}
	}

	private void writeDataLines() {
		errorQueue.drainTo(errorLines, 10000);
		if (!errorLines.isEmpty()) {
			FileUtil.write(new File(dir + "/error-relation.txt"), errorLines);
			errorLines.clear();
		}
	}

	/**
	 * 从读取的行中抽取关系实体
	 */
	private void parseLines() {
		for (String line : lines) {
			ParseLineUtil.parse(line, "\t", fileds);
			if (fileds.size() > 9) {
				try{
					extractRelation(line);
				}catch(Exception e){
					try {
						errorQueue.put(line);
					} catch (InterruptedException e1) {
						LOG.error("parse line into errorQueue error, line is:" + line, e);
					}
				}
				
			} else {
				try {
					errorQueue.put(line);
				} catch (InterruptedException e) {
					LOG.error("parse line into errorQueue error, line is:" + line, e);
				}
			}
			this.fileds.clear();
		}
		this.lines.clear();
	}

	private void extractRelation(String line) {
		Relation r = new Relation();
		
		//id_from, from_type, id_to, to_type, first_start_time, first_terminal_num, source, sys_source, company_id, found_count
		String idFrom = fileds.get(0);
		String fromType = fileds.get(1);
		String idTo = fileds.get(2);
		String toType = fileds.get(3);

		if (ParseLineUtil.filter(idFrom, filters) || ParseLineUtil.filter(idTo, filters)) {
			return;
		}

		try {
			idFrom = validate(idFrom, fromType);
			idTo = validate(idTo, toType);
			if (idFrom == null || idTo == null) {
				throw new Exception("error id with idType");
			}
		} catch (Exception e) {
			return;
		}

        r.setId_from(idFrom);
        r.setForm_type(fromType);
        r.setId_to(idTo);
        r.setTo_type(toType);
        
        r.setFirst_start_time(fileds.get(4));
        r.setFirst_terminal_num(fileds.get(5));
        r.setSource(fileds.get(6));
        r.setSys_source(fileds.get(7));
        r.setCompany_id(fileds.get(8));
        r.setFound_count(fileds.get(9));	
        
        relations.add(r);
        
        return;
	}

	private String validate(String id, String idType) throws Exception {
		if ("-1".equals(id) || "0".equals(id) || id.startsWith(".") || id.startsWith("-")) {
			throw new Exception("error id.");
		}

		if ("1020002".equals(idType) && "00-00-00-00-00-00".equals(id)) {// mac
			throw new Exception("error mac.");
		}

		return CertificationFilter.evaluate(id, idType);
	}

	public void close() {
		this.running = false;
	}
}
