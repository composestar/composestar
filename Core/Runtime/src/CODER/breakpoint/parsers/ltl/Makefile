ROOTDIR=../../../../../

SUBDIRS=

include ${ROOTDIR}common.mk

CLASSES= 	

src: 	LtlLexer.java\
	LtlParser.java \
	LtlParserTokenTypes.java

include ${ROOTDIR}default.mk

LtlLexer.java LtlParser.java LtlTokenTypes.java: grammar\ltl.g
	if exist LtlLexer.java ${DEL} LtlLexer.java
	if exist LtlParser.java ${DEL} LtlParser.java
	if exist LtlLexer.smap ${DEL} LtlLexer.smap
	if exist LtlParser.smap ${DEL} LtlParser.smap
	if exist LtlTokenTypes.java ${DEL} LtlTokenTypes.java
	${ANTLR_RULE} -o . .\grammar\ltl.g
		
clean:
	${DEL} LtlLexer.java LtlParser.java LtlTokenTypes.* grammar\*.java grammar\*.txt grammar\*.class *~ grammar\*~ *.smap ${NULL}

