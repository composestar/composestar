all : 
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing StdCParser.g
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing -glib "StdCParser.g" GnuCParser.g
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing GnuCTreeParser.g
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing -glib "GnuCTreeParser.g" GnuCEmitter.g

trace:
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing StdCParser.g
	java -cp antlr.jar antlr.Tool -trace -o ../wrapper/parsing -glib "StdCParser.g" GnuCParser.g
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing GnuCTreeParser.g
	java -cp antlr.jar antlr.Tool -o ../wrapper/parsing -glib "GnuCTreeParser.g" GnuCEmitter.g
