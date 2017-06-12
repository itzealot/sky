############ build001000 2017-05-10

一、功能说明：
1、根据配置时间段，将default.relation_parquet按ID_FROM,FROM_TYPE,ID_TO,TO_TYPE,SYS_SOURCE,SOURCE,COMPANY_ID分组统计出现次数，
	结果存放在临时表zdr.relation_parquet4；
2、自动将zdr.relation_parquet4表中的所有记录导出到文件（可配置）；
3、读取文件中的内容，插入图数据库中。


二、修改配置文件
#处理线程数
process.pool.size=10
#入图的间隔SLEEP时间，每入完queue.size.limit条关系后，sleep一次
process.sleep=5000
#队列大小
queue.size.limit=20000
#导出生成的关系文件名
relation.file=/appslog1/orientdb/relation.txt
#入图数据库失败的关系文件存放路径
relation.dir=/appslog1/orientdb
#图数据库访问URL
orientdb_url=odb:jdbc:orient:remote:whrzx34/gacenter
#要入图的关系表中开始时间，对于历史关系，建议只入最近三个月或半年的关系，否则时间会比较长且意义不是很大
relation_startdate=20170101
#要入图的关系表中结束时间
relation_enddate=20170508
#impala访问URL
impala_url=jdbc:hive2://whrzx06:21050/;auth=noSasl


三. 运行
sh bin/run.sh

