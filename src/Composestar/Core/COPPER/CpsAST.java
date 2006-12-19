package Composestar.Core.COPPER;

import antlr.CommonAST;
import antlr.Token;
import antlr.collections.AST;

public class CpsAST extends CommonAST
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2547618156802188540L;

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
