#/bin/sh
CSTAR_CORE=`dirname $0`
ANT_HOME=${CSTAR_CORE}/Development/Ant
JAVA_HOME=/usr/java
PATH=${PATH}:${ANT_HOME}/bin
ARGS=$@
if [ -z "${ARGS}" ]; then
	ARGS="select"
fi
BUILDFILE="${CSTAR_CORE}/Development/build_total.xml"

java -cp ${ANT_HOME}/lib/ant-launcher.jar  org.apache.tools.ant.launch.Launcher -buildfile ${BUILDFILE} $ARGS

