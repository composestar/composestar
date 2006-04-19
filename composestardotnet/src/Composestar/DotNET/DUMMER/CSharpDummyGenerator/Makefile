ROOTDIR=../../../../../
include ${ROOTDIR}common.mk

SUBDIRS=Parse

generate:
	${RECURSE_MAKE_RULE}
	
src: generate
	-${DEL} error.log ${NULL}
	"devenv.exe" CSharpDummyGenerator.sln /rebuild Release /out error.log
	@type error.log

clean:
	${DEL} error.log ${NULL}