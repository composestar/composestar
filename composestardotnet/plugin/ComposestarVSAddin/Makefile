ROOTDIR=../../
include ${ROOTDIR}common.mk

src:
	-$rDEL} error.log ${NULL}
	("devenv.exe" ComposestarVSAddin.sln /rebuild Release /out error.log || (@type error.log && _fail 2>NUL))

clean:
	${DEL} error.log ${NULL}

include ${ROOTDIR}default.mk
