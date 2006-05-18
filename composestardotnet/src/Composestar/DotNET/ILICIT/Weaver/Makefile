ROOTDIR=../../../../../
include ${ROOTDIR}common.mk

src:
	-${DEL} error.log ${NULL}
	("devenv.exe" Weavers.sln /rebuild Release /out error.log || (@type error.log && _fail 2>NUL))

clean:
	${DEL} error.log ${NULL}

include ${ROOTDIR}default.mk
