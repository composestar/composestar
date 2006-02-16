ROOTDIR=../../../../../
include ${ROOTDIR}common.mk

SUBDIRS=

CLASSES=DotNETParameterInfoHandler.class \
	DotNETFieldInfoHandler.class \
	DotNETMethodInfoHandler.class \
	DotNETTypeHandler.class \
	DocumentHandler.class \
	DotNETCollectorRunner.class \
	AttributeCollector.class

include ${ROOTDIR}default.mk

test: all
	java -classpath $(ROOTDIR) Composestar.CTAdaption.TYM.TypeCollector.DocumentHandler
