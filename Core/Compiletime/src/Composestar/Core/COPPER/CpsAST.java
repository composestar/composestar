package Composestar.Core.COPPER;

import antlr.*;
import antlr.collections.*;

public class CpsAST extends CommonAST
{
	protected int line;

	public void initialize(Token tok)
	{
		super.initialize(tok);
		if (tok != null)
		{
			line = tok.getLine();
		}
	}

	public void initialize(AST tok)
	{
		super.initialize(tok);
		if (tok != null)
		{
			line = tok.getLine();
		}
	}

	public void initialize(int type, String txt)
	{
		super.initialize(type, txt);
	}

	public int getLine()
	{
		return line;
	}
}
