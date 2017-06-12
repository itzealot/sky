#!/bin/sh
print_usage ()
{
  echo "Usage: sh run.sh COMMAND"
  echo "where COMMAND is one of the follows:"
  echo "  -> hbaseToolsDeleteCli -t xxx -f xxx -m r|c -sp xxx"
  echo "  -> delColCli -t xxx -f xxx -m r|c -col xxx -sp xxx"
  echo "  -> modifyColCli -t xxx -f xxx -m r|c -dc xxx -ac xxx -sp xxx"
  echo "  -> putColCli -t xxx -f xxx -m r|c -col xxx -value xxx -type xxx -sp xxx"
  echo "  -> modifyRangeValueCli -t xxx -f xxx -m r|c -col xxx -value xxx -type xxx -sp xxx -range xxx-xxx"
  echo "  -> hbaseToolsGenRowkeysCli -sf xxx"
  echo "  -> rt2HbaseCli -rt_file_dir xxx -t xxx -cid xxx"
  echo "  -> cert2HbaseCli -cert_file_dir xxx -t xxx -cid xxx"
  echo "  -> FileRelation2HbaseCli"
echo "  -> Table2TableCli -st source_table -dt dest_table -srow start_row"
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

if  [ "$COMMAND" = "hbaseToolsDeleteCli" ];then
  CLASS=com.surfilter.mass.tools.cli.HbaseToolsDeleteCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &
  
elif  [ "$COMMAND" = "hbaseToolsGenRowkeysCli" ];then
  CLASS=com.surfilter.mass.tools.cli.HbaseToolsGenRowkeysCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &

elif  [ "$COMMAND" = "delColCli" ];then
  CLASS=com.surfilter.mass.tools.cli.HbaseToolsDeleteColCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &
  
elif  [ "$COMMAND" = "modifyColCli" ];then
  CLASS=com.surfilter.mass.tools.cli.HbaseToolsModifyColCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &
  
elif  [ "$COMMAND" = "putColCli" ];then
  CLASS=com.surfilter.mass.tools.cli.HbaseToolsPutColCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &

elif  [ "$COMMAND" = "rt2HbaseCli" ];then
  CLASS=com.surfilter.mass.tools.cli.FileRt2HbaseCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &

elif  [ "$COMMAND" = "cert2HbaseCli" ];then
  CLASS=com.surfilter.mass.tools.cli.FileCert2HbaseCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &

elif  [ "$COMMAND" = "exImportCertRelationCli" ];then
  CLASS=com.surfilter.mass.tools.cli.ExImportCertRelationCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &
  
elif  [ "$COMMAND" = "FileRelation2HbaseCli" ];then
  CLASS=com.surfilter.mass.tools.cli.FileRelation2HbaseCli
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
  
elif  [ "$COMMAND" = "modifyRangeValueCli" ];then
  CLASS=com.surfilter.mass.tools.cli.HbaseToolsmodifyRangeValueCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &

elif  [ "$COMMAND" = "Table2TableCli" ];then
  CLASS=com.surfilter.mass.tools.cli.Table2TableCli
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &

else
  CLASS=$COMMAND
fi

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
tail -f $STDOUT_FILE
