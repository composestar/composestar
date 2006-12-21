package Composestar.C.CONE;

public class SubstituteSemantic extends Semantic
{

	private String type = "Substitute";

	private String advice = "";

	private boolean afterAdvice = false;

	private boolean beforeAdvice = true;

	private boolean redirectMessage = true;

	private boolean needsHeaderFiles = false;

	public String getBeforeAdvice()
	{
		String conditionCode = conditionCode();
		String parameters = "";
		for (int i = 0; i < numberOfParametersMF(); i++)
		{
			if (i == numberOfParametersMF() - 1)
			{
				parameters += parameterName(i);
			}
			else
			{
				parameters += parameterName(i) + ',';
			}
		}
		if (conditionCode.equals(""))
		{
			advice = substitutionFunction() + "(" + parameters + ");\n";
		}
		else
		{
			advice = "if(" + disableOperatorCode() + conditionCode + ")" + substitutionFunction() + "(" + parameters
					+ ");\n";
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
		return "";
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
