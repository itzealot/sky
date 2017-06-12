############ build003000 -> build004000

1.	修改表结构
执行表结构的更新语句，即先将iDriller升级到build004000

2.	文件导出
使用从 iDriller 中导出 relation与certification 到文件(也可以从 mysql中导出relation)

2.1从iDriller 中导出
mkdir -p /appslog/iDriller_export
mkdir -p /appslog/iDriller_export/relation
mkdir -p /appslog/iDriller_export/certification

# 后台运行导出 relation
nohup iDriller-shell -q "select * from relation" -B -o /appslog/iDriller_export/relation/ relation.txt &
# 后台运行导出 certification
nohup iDriller-shell -q "select * from certification" -B -o /appslog/iDriller_export/certification/ certification.txt &

2.2 从 mysql 中导出relation
mkdir –p /appslog/mysql_export/relation

# 授予权限给 mysql 用户
chown mysql:mysql /appslog/mysql_export

# 进入 mysql shell，执行查询语句
select * from relation into outfile "/appslog/mysql_export/relation/relation.txt";

3.	修改配置文件
#common config
application.name=GamassHbaseToolsApp
#hbase config
# hbase 的zookeeper集群主机列表
hbase.zk.quorum=rzx162,rzx164,rzx166
# hbase zookeeper 端口
hbase.zk.port=2181
# hbase master
# hbase 主节点及60000端口，使用jps 有显示 HBaseMaster 即为主节点
hbase.master=rzx162:60000
# hbase主节点对应的hdfs目录
hbase.rootdir=hdfs://rzx162:9000/hbase

# redis
# redis 配置，主机|端口
redis.conf=es4|6379

# 需要插入的表名称，生产环境需要修改为relation
copy.relation.to.hbase.table=relation
# the hbase's zookeeper url
# hbase zookeeper集群列表
copy.relation.to.hbase.zookeeper.url=rzx162:2181,rzx164:2181,rzx166:2181
# consumer's size, thread pool size
# 默认消费线程数
copy.relation.to.hbase.pool.size=10
# queue size save relation rows
copy.relation.to.hbase.queue.size.limit=20000
# beyond queue size limit then sleep {num} ms
copy.relation.to.hbase.sleep=1000
# 导出的 relation 文件路径
copy.relation.to.hbase.source.file=/appslog/hbase_data_test/relation/relation.txt
# 错误解析的 relation 保存目录
copy.relation.to.hbase.target.dir=/appslog/hbase_data_test/log/relation

4. 运行
4.1 导入relation
# 配置修改完成后运行
sh bin/run.sh FileRelation2HbaseCli

4.2 导入certification
sh bin/run.sh cert2HbaseCli -cert_file_dir /appslog/iDriller_export/certification -t certification -cid MULL
