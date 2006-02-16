package Composestar.Core.COPPER;

/**
 * This class Is a verrrrrry brute hack into antlr.
 * Because we use our defined tokens antlr's default token can't be redefined
 * Since we want to redefine this to add source reference support we overwrite the factory
 * Which facilitates antlr
 * To do this we throw away all notification of specialized classes
 */
import antlr.*;
import antlr.collections.*;

public class CpsASTFactory extends ASTFactory
{
	public AST dup(AST t)
	{
		return create(t);
	}

	public AST create()
	{
		return new CpsAST();
	}

	public AST create(AST tok)
	{
		CpsAST ast = new CpsAST();
		ast.initialize(tok);
		return ast;
	}

	public AST create(int type)
	{
		CpsAST ast = new CpsAST();
		ast.setType(type);
		return ast;
	}

	public AST create(int type, String string)
	{
		CpsAST ast = new CpsAST();
		ast.initialize(type,string);
		return ast;
	}

	public AST create(Token tok, String string){
		return create(tok);
	}

	public AST create(String string)
	{
		return create();
	}

	public AST create(int type, String string, String className)
	{
		return create(type,string);
	}

	public AST create(Token tok)
	{
		CpsAST ast = new CpsAST();
		ast.initialize(tok);
		return ast;
	}

}
