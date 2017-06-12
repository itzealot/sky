package com.surfilter.gamass.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.surfilter.gamass.conf.Constant;
import com.surfilter.gamass.conf.MassConfiguration;
import com.surfilter.gamass.entity.Relation;
import com.surfilter.gamass.util.ParseRelationUtil;
import com.surfilter.mass.tools.util.Threads;

public class RelationParseTask implements Runnable {

	private BlockingQueue<Relation> queue;
	private volatile boolean isRunning = true;
	private String certDir;
	private String relationDir;
	private String certTrackDir;
	private String dst;
	private String cetificationTableName;
	private String relationTableName;
	private String hbaseParams;
	private String serversInfo;
	private boolean versionIsFxj;

	public RelationParseTask(BlockingQueue<Relation> queue, MassConfiguration conf) {
		this.queue = queue;
		initByConf(conf);
		initDstDir();
	}

	private void initByConf(MassConfiguration conf) {
		String dst = conf.get(Constant.WRITE_DST_DIR);

		if (!dst.endsWith("/")) {
			this.dst = dst + "/";
		}

		this.cetificationTableName = conf.get(Constant.WRITE_HBASE_CERTIFICATION_TABLE);
		this.relationTableName = conf.get(Constant.WRITE_HBASE_RELATION_TABLE);
		this.hbaseParams = conf.get(Constant.WRITE_HBASE_PARAMS).replaceAll(";", ",");
		this.serversInfo = conf.get(Constant.WRITE_REDIS);
		this.versionIsFxj = "true".equals(conf.get(Constant.VERSION_IS_FXJ));
	}

	private void initDstDir() {
		this.certDir = this.dst + "certification";
		this.relationDir = this.dst + "relation";
		this.certTrackDir = this.dst + "certTrack";

		new File(this.certDir).mkdirs();
		new File(this.relationDir).mkdirs();
		new File(this.certTrackDir).mkdirs();
	}

	@Override
	public void run() {
		while (isRunning) {
			List<Relation> msgs = new ArrayList<>(Constant.RELATION_BATCH_SIZE);
			queue.drainTo(msgs, Constant.RELATION_BATCH_SIZE);

			if (!msgs.isEmpty()) {
				ParseRelationUtil.parseAndWriteRelation(msgs, relationDir, hbaseParams, relationTableName,
						versionIsFxj);
				ParseRelationUtil.parseAndWriteCertification(msgs, certDir, hbaseParams, cetificationTableName,
						serversInfo, versionIsFxj);
				ParseRelationUtil.parseAndWriteCertificationTrack(msgs, certTrackDir);

				msgs.clear(); // 快速释放内存
				msgs = null;
			} else {
				Threads.sleep(100);
				msgs = null;
			}
		}
	}

	public void shutdown() {
		this.isRunning = false;
	}

}
