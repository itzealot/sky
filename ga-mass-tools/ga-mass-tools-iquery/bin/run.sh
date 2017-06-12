#!/bin/sh
print_usage ()
{
  echo "  -> RegData2IQueryCli"
  echo "  -> LiushuiData2IQueryCli"
  echo "  -> Data2IQueryCli"
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
HEAP_OPTS="-Xmx1000m -XX:PermSize=128m -XX:MaxPermSize=256m"

CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar
CLASSPATH=${CLASSPATH}:conf

# add lib jars
for f in lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

if  [ "$COMMAND" = "RegData2IQueryCli" ];then
  CLASS=com.surfilter.mass.tools.cli.RegData2IQueryCli
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "Data2IQueryCli" ];then
  CLASS=com.surfilter.mass.tools.cli.Data2IQueryCli
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "LiushuiData2IQueryCli" ];then
  CLASS=com.surfilter.mass.tools.cli.LiushuiData2IQueryCli
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
echo "输出如下："
tail -f $STDOUT_FILE