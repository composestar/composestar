ROOTDIR=../../../
include ${ROOTDIR}common.mk

src:
	cd CODER&&cd visualDebugger&&cd parsers&& ${MAKE}&& cd ..&&cd ..&&cd ..
	-${DEL} error.log ${NULL}
	"devenv.exe" Runtime.sln /rebuild Release /out error.log
	@type error.log

clean:
	${DEL} error.log ${NULL}

include ${ROOTDIR}default.mk
