#!/bin/sh
print_usage ()
{
  echo "Usage: sh run.sh COMMAND"
  echo "where COMMAND is one of the follows:"
  echo "  -> RedisCertificationUpdateApp"
  echo "  -> RowKeyTimeCompareApp"
  echo "  -> RedisCertificationChangeApp"
  echo "  -> RedisHyperLogApp"
  exit 1
}

if [ $# = 0 ] || [ $1 = "help" ]; then
  print_usage
fi

COMMAND=$1
shift

if [ "$JAVA_HOME" = "" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi


cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
STDOUT_FILE=$DEPLOY_DIR/logs/stdout.log
# 检查logs目录存不存在
if [ ! -d $DEPLOY_DIR/logs ]; then
    mkdir $DEPLOY_DIR/logs
fi

JAVA=$JAVA_HOME/bin/java
HEAP_OPTS="-Xmx4g -XX:PermSize=512m -XX:MaxPermSize=256m"

CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar
CLASSPATH=${CLASSPATH}:conf

for f in *.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

for f in lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
  LIB_JARS=${LIB_JARS}:$f;
done

if  [ "$COMMAND" = "RedisCertificationUpdateApp" ];then
  CLASS=com.surfilter.mass.tools.RedisCertificationUpdateApp
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &

elif  [ "$COMMAND" = "RedisCertificationChangeApp" ];then
  CLASS=com.surfilter.mass.tools.RedisCertificationChangeApp
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
 
elif  [ "$COMMAND" = "RowKeyTimeCompareApp" ];then
  CLASS=com.surfilter.mass.tools.RowKeyTimeCompareApp
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &

  elif  [ "$COMMAND" = "RedisHyperLogApp" ];then
  CLASS=com.surfilter.mass.tools.RedisHyperLogApp
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &

else
  CLASS=$COMMAND
fi

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
tail -f $STDOUT_FILE
