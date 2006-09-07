ROOTDIR=../../../../../../
include ${ROOTDIR}common.mk

SUBDIRS=

src: csharp.g
	"${CURDIR}/antlr-2.7.2.exe" csharp.g

clean:
	${DEL} CSharpLexer.cs CSharpParser.cs CSharpTokenTypes.cs ${NULL}