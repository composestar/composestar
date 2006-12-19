#/bin/sh
export ANT_HOME=./Development/Ant
export JAVA_HOME=/usr/java
export PATH=${PATH}:${ANT_HOME}/bin
java -cp Development/Ant/lib/ant-launcher.jar  org.apache.tools.ant.launch.Launcher -buildfile Development/build_total.xml $@
