#/bin/sh
export ANT_HOME=./Development/Ant
export JAVA_HOME=/usr/java
export PATH=${PATH}:${ANT_HOME}/bin
export ECLIPSE_HOME=/usr/local/eclipse
java -cp Development/Ant/lib/ant-launcher.jar  org.apache.tools.ant.launch.Launcher -buildfile Development/build_total.xml $@
