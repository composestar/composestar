package Composestar.Core.Master.Config;

import java.io.Serializable;

public class CompilerConverter implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2071985122483867847L;

	private String name;

	private String expression;

	private String replaceBy;

	public CompilerConverter()
	{

	}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		name = inName;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String inExpression)
	{
		expression = inExpression;
	}

	public String getReplaceBy()
	{
		return replaceBy;
	}

	public void setReplaceBy(String inReplaceBy)
	{
		replaceBy = inReplaceBy;
	}
}
