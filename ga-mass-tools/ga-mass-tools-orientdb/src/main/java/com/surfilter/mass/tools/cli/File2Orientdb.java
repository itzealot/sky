package com.surfilter.mass.tools.cli;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.commons.utils.conf.MassConfiguration;
import com.surfilter.mass.tools.conf.SysConstants;
import com.surfilter.mass.tools.services.OrientdbCliThread;
import com.surfilter.mass.tools.services.Relation2File;
import com.surfilter.mass.tools.util.FileUtil;
import com.surfilter.mass.tools.util.Threads;

public class File2Orientdb {

	private static final Logger LOG = LoggerFactory.getLogger(File2Orientdb.class);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MassConfiguration massConfiguration = new MassConfiguration();		


		//要入图的关系表中开始时间，对于历史关系，建议只入最近三个月或半年的关系，否则时间会比较长且意义不是很大
		String startDate = massConfiguration.get("relation_startdate");
		//要入图的关系表中结束时间
		String endDate = massConfiguration.get("relation_enddate");
		//处理线程数
		int poolSize = massConfiguration.getInt("process.pool.size", 10);
		//队列大小
		int queueSizeLimit = massConfiguration.getInt("queue.size.limit", 20000);		
		//入图的间隔SLEEP时间，每入完queue.size.limit条关系后，sleep一次
		int sleep = massConfiguration.getInt("process.sleep", 1000);
		//导出生成的关系文件名
		String fileName = massConfiguration.get("relation.file", "/appslog1/orientdb/relation.txt");
		//图数据库访问URL
		String orientUrl = massConfiguration.get("orientdb_url");
		//impala访问URL
		String impalaUrl = massConfiguration.get("impala_url");
		//入图数据库失败的关系文件存放路径
		String dir = massConfiguration.get("relation.dir", "/appslog1/orientdb");
			
		
		LOG.info("relation start-end date: " + startDate + " - " + endDate);
		
		//将关系合并导出文件
		relation2File(startDate, endDate, impalaUrl, fileName);
		
		//读取文件逐行入图
		readFile2Orientdb(poolSize, queueSizeLimit, sleep, orientUrl, dir, fileName);
		
	}


	//将关系合并导出文件
	public static void relation2File(String startTime, String endTime, String impalaUrl, String fileName){
		Relation2File relation2file = new Relation2File(startTime, endTime, impalaUrl);
		
		relation2file.init();
		
		relation2file.convertTable();
		
		relation2file.export2File(fileName);
		
		relation2file.close();
		
	}
	
	
	//读取文件逐行入图
	public static void readFile2Orientdb(int poolSize, int queueSizeLimit, int sleep, String orientUrl, String dir, String fileName){

		LOG.info("从文件中读取relation记录到orientdb手动任务开始,在配置文件conf/conf.properties中修改配置参数...");

		BlockingQueue<String> queue = new LinkedBlockingQueue<>();
		
		
		// 根据线程池大小创建线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

		OrientdbCliThread[] threads = new OrientdbCliThread[poolSize];


		


		for (int i = 0; i < poolSize; i++) {
			threads[i] = new OrientdbCliThread(queue, orientUrl, dir, SysConstants.ACCOUNT_FILTER, sleep);
			threadPool.execute(threads[i]);
		}

		try {
			FileUtil.read(queue, new File(fileName), sleep, queueSizeLimit);
		} catch (Exception e) {
			LOG.error("读取文件中的relation记录到orientdb手动任务任务失败!", e);
		}

		while (!queue.isEmpty()) {
			Threads.sleep(2000);
			LOG.info("wait to finish dealing with the queue........");
		}

		// close the thread and the pool shut down
		for (int i = 0; i < poolSize; i++) {
			threads[i].close();
		}
		threadPool.shutdown();
	}

}
