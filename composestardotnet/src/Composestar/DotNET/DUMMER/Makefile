ROOTDIR=../../../../
include ${ROOTDIR}common.mk

CLASSES=CSharpDummyEmitter.class \
	JSharpDummyEmitter.class \
	JSharpLexer.class \
	JSharpRecognizer.class \
	JSharpTokenTypes.class

all: JSharpLexer.java \
     JSharpRecognizer.java \
     JSharpTokenTypes.java \
     compile

include ${ROOTDIR}default.mk

JSharpLexer.java JSharpRecognizer.java JSharpTokenTypes.java: grammars/jsharp.g
	${ANTLR_RULE} -o . grammars/jsharp.g
