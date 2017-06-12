#!/bin/sh
cd `dirname $0`
cd ..
deleteCertification(){
RDB_HOME=`pwd`
# 判断当前目录rdb.dump存不存在，不存在则在/appslog/redis目录下找
if [ -f "$RDB_HOME/dump.rdb" ] ; then
filepath=$RDB_HOME/dump.rdb
else
echo "$RDB_HOME下不存在dump.rdb文件，改用/appslog/redis/dump.rdb"
filepath=/appslog/redis/dump.rdb
fi

for f in $RDB_HOME/*.jar; do
  if [  `echo $f | grep redis`  ];then
        JAR_NAME=$f;
  fi
done

echo "请输入一个日期范围，格式：YYYY/MM/dd"
read -p "开始日期：" startTime
read -p "结束日期：" endTime
if [ $1 == "yes" ] ; then
nohup java -jar $JAR_NAME $filepath $startTime $endTime "true" &>nohup.out &
elif [ $1 == "no" ] ; then
nohup java -jar $JAR_NAME $filepath $startTime $endTime &>nohup.out &
fi
tail -100f nohup.out
}



echo "for the clean of mac whose second element is not 0 or 4 or 8 or C，please input 1"
echo "for the clean of certification whose last update time is over specified month，please input 2"
echo "to delete and transfer outdated mac to hbase, please input 3"
echo "to calculate the sum of the mac in redis,please input 4"
echo "to delete outdated certification using redis-cli,please input 5"
read -p "input:" input
if [ $input == 1 ] ; then
rm -f mkey.dump;for mkey in `redis-cli keys m_*`; do if [[ ! "048C" =~ ${mkey:3:1} ]]; then echo "$mkey" >> mkey.dump; fi; done;cat mkey.dump |xargs redis-cli del
elif [ $input == 2 ] ; then
#    read -p "input yes or no: " isTransfer
#    if [ $isTransfer == "yes" -o $isTransfer == "no" ] ; then
    deleteCertification "no"
#    else
#    echo "输入错误，程序终止"
#    fi
elif [ $input == 3 ] ; then
    deleteCertification "yes"
elif [ $input == 4 ] ; then
    sum=0;for mkey in `redis-cli keys m_*` ; do sum=$(($sum+`redis-cli hlen $mkey`)) ; done;echo $sum
elif [ $input == 5 ] ; then
    sh bin/redisCliSimulator.sh
else
    echo "输入错误，程序终止"
fi




