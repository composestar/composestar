/*
 * Created on 6-okt-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.C.CONE;

/**
 * @author johan TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class ErrorSemantic extends Semantic
{
	private String type = "Error";

	private String advice = "";

	private boolean afterAdvice = false;

	private boolean beforeAdvice = true;

	private boolean redirectMessage = false;

	private boolean needsHeaderFiles = true;

	public ErrorSemantic()
	{}

	public String getBeforeAdvice()
	{
		String conditionCode = conditionCode();
		if (conditionCode.equals(""))
		{
			advice = "//Error: no condition set, not possible to check for errors!";
		}
		else
		{
			advice = "assert(" + conditionCode + ");\n";
		}
		return advice;
	}

	public boolean afterAdvice()
	{
		return afterAdvice;
	}

	public boolean beforeAdvice()
	{
		return beforeAdvice;
	}

	public boolean redirectMessage()
	{
		return redirectMessage;
	}

	public String headerFile()
	{
		return "assert";
	}

	public boolean needsHeaderFiles()
	{
		return needsHeaderFiles;
	}

	public String getType()
	{
		return type;
	}
}
