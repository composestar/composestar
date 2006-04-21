ROOTDIR=../../../../../
include ${ROOTDIR}common.mk

SUBDIRS=Parse

src: generate CSharpDummyGenerator.cs Gen/CSharpGen.cs Gen/LineWriter.cs Parse/CSharpAST.cs Parse/CSharpParser.cs Parse/CSharpLexer.cs Parse/CSharpTokenTypes.cs Parse/LineNumberAST.cs Graph/CodeGraph.cs Graph/Collections.cs Graph/SymbolTables.cs Graph/SymbolCollections.cs Walk/GraphParserBase.cs
	-${DEL} error.log ${NULL}
	"devenv.exe" CSharpDummyGenerator.sln /rebuild Release /out error.log
	@type error.log

generate: Parse/csharp.g
	${RECURSE_MAKE_RULE}
	
clean:
	${DEL} error.log ${NULL}