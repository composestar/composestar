ROOTDIR=../../../../../../../

SUBDIRS=

include ${ROOTDIR}common.mk

CLASSES= 	

src: RegLexer.java\
	RegParser.java \
	RegParserTokenTypes.java \
     compile

include ${ROOTDIR}default.mk

RegLexer.java RegParser.java RegTokenTypes.java: grammar\Reg.g
	if exist RegLexer.java ${DEL} RegLexer.java
	if exist RegParser.java ${DEL} RegParser.java
	if exist RegLexer.smap ${DEL} RegLexer.smap
	if exist RegParser.smap ${DEL} RegParser.smap
	if exist RegTokenTypes.java ${DEL} RegTokenTypes.java
	${ANTLR_RULE} -o . grammar\Reg.g
		
EXTRA_CLEAN= ${DEL} RegLexer.java RegParser.java RegTokenTypes.* grammar\*.java grammar\*.txt grammar\*.class *~ grammar\*~ *.smap ${NULL}
