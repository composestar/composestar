ROOTDIR=../../../../
include ${ROOTDIR}common.mk

SUBDIRS=applet

CLASSES=CpsRepositoryBuilder.class \
	CpsSrcPosLexer.class \
	CpsPosLexer.class \
        SourceExtractor.class \
	COPPER.class \
	CPSFileParser.class \
        PosToken.class \
  	Splitter.class \
	CpsLexer.class \
	CpsParser.class \
	CpsTokenTypes.class \
	CpsTreeWalkerTokenTypes.class \
	CpsTreeWalker.class \
	CpsSrcLexer.class \
	CpsSrcParser.class \
	CpsSrcTokenTypes.class \
	AstManipulator.class \
	SyntacticSugarExpander.class \
	Piece.class \
	CpsAST.class \
	CpsASTFactory.class \
	PathHelper.class
	

all: CpsLexer.java \
     CpsParser.java \
     CpsTokenTypes.java \
     CpsTreeWalker.java \
     CpsTreeWalkerTokenTypes.java \
     CpsSrcLexer.java \
     CpsSrcParser.java \
     CpsSrcTokenTypes.java \
     compile

include ${ROOTDIR}default.mk

CpsLexer.java CpsParser.java CpsTokenTypes.java: grammar/cps.g
	${ANTLR_RULE} -o . grammar/cps.g
	
CpsTreeWalker.java CpsTreeWalkerTokenTypes.java: grammar/cpsw.g
	${ANTLR_RULE} -o . grammar/cpsw.g
	
CpsSrcLexer.java CpsSrcParser.java CpsSrcTokenTypes.java: grammar/cpssrc.g
	${ANTLR_RULE} -o . grammar/cpssrc.g
	
	
EXTRA_CLEAN=${DEL} CpsLexer.java CpsParser.java CpsTokenTypes.* CpsTreeWalker.java CpsTreeWalkerTokenTypes.* CpsSrcLexer.java CpsSrcParser.java CpsSrcLexer.java  CpsSrcTokenTypes.* *.smap ${NULL}
