#!/bin/sh
print_usage ()
{
  echo " -> ZtryDetailCatchApp path"
  echo " -> ZtryCatchApp"
  echo " -> ZtryUrlCatchApp"
  echo " -> ZtryRevokeCatchApp"
  echo " -> ZtryRevokeUrlCatchApp"
  echo " -> VehicleDataCatchApp"
  echo " -> VehicleDataCatchTimerApp"
  echo " -> AllCarInfoCatchApp"
  echo " -> PeopleHotelInfoCatchApp"
  echo " -> ZtryDetai2DBApp"
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

if  [ "$COMMAND" = "ZtryUrlCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.ZtryUrlCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "ZtryCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.ZtryCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "ZtryRevokeCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.ZtryRevokeCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "ZtryRevokeUrlCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.ZtryRevokeUrlCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "VehicleDataCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.VehicleDataCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "AllCarInfoCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.AllCarInfoCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "PeopleHotelInfoCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.PeopleHotelInfoCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "VehicleDataCatchTimerApp" ];then
  CLASS=com.surfilter.mass.tools.cli.VehicleDataCatchTimerApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "ZtryDetailCatchApp" ];then
  CLASS=com.surfilter.mass.tools.cli.ZtryDetailCatchApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &
fi

if  [ "$COMMAND" = "ZtryDetai2DBApp" ];then
  CLASS=com.surfilter.mass.tools.cli.ZtryDetai2DBApp
  params=$@
  nohup "$JAVA" -Djava.awt.headless=true $HEAP_OPTS -classpath "$CLASSPATH" $CLASS $params > $STDOUT_FILE 2>&1 &
fi

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
echo "输出如下："
tail -f $STDOUT_FILE