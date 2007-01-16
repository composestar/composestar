package Composestar.C.CONE;

import java.util.Iterator;
import java.util.List;

import Composestar.C.LAMA.CAnnotation;
import Composestar.C.LAMA.CFile;
import Composestar.C.LAMA.CMethodInfo;
import Composestar.C.LAMA.CParameterInfo;
import Composestar.C.LAMA.CType;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.DisableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElementAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;

public abstract class Semantic
{

	protected String filtertype;

	protected Filter filter;

	protected int elementNumber;

	protected Concern concern;

	protected List parameters = null;

	protected String typeString = "";

	protected CFile file;

	protected CMethodInfo method;

	/**
	 * @return Code that creates the reified message struct for the matching
	 *         function
	 */
	protected String reifiedMessageCode()
	{
		String parameters = "";
		String code = "";
		CParameterInfo parameter = null;
		// CMethodInfo method
		// =(CMethodInfo)file.getMethodInfo(matchingFunction());
		if (method != null)
		{
			code += "struct message msg;\n";
			code += "void* temp;\n";
			List params = method.getParameters();
			Iterator par = params.iterator();
			int numberOfParameters = 0;
			if (par.hasNext())
			{
				parameter = (CParameterInfo) par.next();
				code += setArgument(parameter, numberOfParameters);
				numberOfParameters++;
				typeString += typeString(parameter.getParameterTypeString());
				parameters += parameter.getParameterTypeString() + " " + parameter.getUnitName();
			}
			while (par.hasNext())
			{
				parameter = (CParameterInfo) par.next();
				code += setArgument(parameter, numberOfParameters);
				numberOfParameters++;
				typeString += typeString(parameter.getParameterTypeString());
				parameters += "," + parameter.getParameterTypeString() + " " + parameter.getUnitName();
			}
			code += setArgumentNumber(numberOfParameters);
			code += setType(typeString);
			return code;
		}
		else
		{
			return code;
		}
	}

	/**
	 * @return The name of the condition function
	 */
	protected String conditionCode()
	{
		String conditionPart = "";
		FilterElement fil = filter.getFilterElement(elementNumber);
		if (fil.filterElementAST.conditionPart instanceof ConditionVariable)
		{// ConditionExpression){
			FilterElementAST fast = fil.filterElementAST;
			ConditionVariable conditionVar = (ConditionVariable) fast.getConditionPart();
			conditionPart = conditionVar.getCondition().name + "()";
		}
		return conditionPart;
	}

	/**
	 * @return ! if the operator used in the conditionexpression of the filter
	 *         is ~>
	 */
	protected String disableOperatorCode()
	{
		String code = "";
		if (filter.getFilterElement(elementNumber).getEnableOperatorType() instanceof DisableOperator)
		{
			code = "!";
		}
		return code;
	}

	/**
	 * @return The name of the parameter of the matching function
	 * @param index of the parameter from the function
	 */
	protected String parameterName(int index)
	{
		String parameterName = "";
		CMethodInfo method = file.getMethodInfo(matchingFunction());
		if (method != null)
		{
			List params = method.getParameters();
			if (params.size() <= index)
			{
				return "Error";
			}
			parameterName = ((CParameterInfo) params.get(index)).getUnitName();
		}
		return parameterName;
	}

	/**
	 * @return The type of the annotation of the parameter of the matching
	 *         function
	 * @param index of the parameter from the function
	 */
	protected CAnnotation parameterAnnotation(int index)
	{
		CParameterInfo parameter = null;
		CAnnotation parameterAnno = null;
		CMethodInfo method = file.getMethodInfo(matchingFunction());
		if (method != null)
		{
			List params = method.getParameters();
			if (params.size() <= index)
			{
				return null;
			}
			parameter = (CParameterInfo) params.get(index);
			/**
			 * TODO here is hard coded that we need to pick the first/ and only
			 * possible annotation However when annotations are extended this
			 * can give troubles
			 */
			if (parameter.getAnnotations().size() > 0)
			{
				parameterAnno = (CAnnotation) parameter.getAnnotations().get(0);
			}
		}
		return parameterAnno;
	}

	protected CAnnotation fileAnnotation()
	{
		if (file.annotationInstances.size() > 0)
		{
			return (CAnnotation) file.annotationInstances.get(0);
		}
		else
		{
			return null;
		}
	}

	protected CAnnotation methodAnnotation()
	{
		CMethodInfo method = file.getMethodInfo(matchingFunction());
		if (method.getAnnotations().size() > 0)
		{
			return (CAnnotation) method.getAnnotations().get(0);
		}
		else
		{
			return null;
		}
	}

	protected String annotationType(CAnnotation anno)
	{
		if (anno.getType() instanceof CFile)
		{
			/**
			 * the type of a file annotation is a special one, being file,
			 * however because we know that we return the file name here
			 */
			return ((CFile) anno.getType()).Name;
		}
		if (anno.getType() instanceof CType)
		{
			return ((CType) anno.getType()).name;
		}
		return null;
	}

	protected String annotationValue(CAnnotation anno)
	{
		return anno.m_value.substring(1, anno.m_value.length() - 1);
	}

	/**
	 * @return The type of the parameter of the matching function
	 * @param index of the parameter from the function
	 */
	protected String parameterTypeMF(int index)
	{
		String parameterType = "";
		CMethodInfo method = file.getMethodInfo(matchingFunction());
		if (method != null)
		{
			List params = method.getParameters();
			if (params.size() <= index)
			{
				return "Error";
			}
			parameterType = ((CParameterInfo) params.get(index)).getParameterTypeString();
			// parameters+=parameter.getParameterTypeString()+ " " +
			// parameter.getUnitName();
		}
		return parameterType;
	}

	/**
	 * @return The number of parameters of the matching function
	 */
	protected int numberOfParametersMF()
	{
		CMethodInfo method = file.getMethodInfo(matchingFunction());
		if (method != null)
		{
			List params = method.getParameters();
			return params.size();
		}
		return -1;
	}

	protected String parameters()
	{
		String parameters = "";
		if (numberOfParametersMF() > 0)
		{
			parameters += parameterName(0);
		}
		for (int i = 1; i < numberOfParametersMF(); i++)
		{
			parameters += "," + parameterName(i);
		}
		return parameters;
	}

	/**
	 * @return The type of the parameter of the substitution function
	 * @param index of the parameter from the function
	 */
	protected String parameterTypeSF(int index)
	{
		String parameterType = "";
		CMethodInfo method = file.getMethodInfo(substitutionFunction());
		if (method != null)
		{
			List params = method.getParameters();
			if (params.size() <= index)
			{
				return "Error";
			}
			parameterType = ((CParameterInfo) params.get(index)).getParameterTypeString();
			// parameters+=parameter.getParameterTypeString()+ " " +
			// parameter.getUnitName();
		}
		return parameterType;
	}

	/**
	 * @return The number of parameters of the substitution function
	 */
	protected int numberOfParametersSF()
	{
		CMethodInfo method = file.getMethodInfo(substitutionFunction());
		if (method != null)
		{
			List params = method.getParameters();
			return params.size();
		}
		return -1;
	}

	/**
	 * @return Name of the matching function
	 */
	protected String matchingFunction()
	{
		return method.getName();
	}

	/**
	 * @return Name of the substitution function
	 */
	protected String substitutionFunction()
	{
		return ((SubstitutionPart) (filter.getFilterElement(elementNumber).getMatchingPattern().getSubstitutionParts()
				.elementAt(0))).getSelector().getName();
	}

	private String setArgumentNumber(int totalNumberOfParameters)
	{
		return "msg.argumentNumber=" + totalNumberOfParameters + ";";
	}

	private String setType(String typeString)
	{
		return "msg.type=\"" + typeString + "\";\n";
	}

	private String typeString(String typeString)
	{
		String type = "";
		if (typeString.equals("int"))
		{
			type = "i";
		}
		else if (typeString.equals("char*"))
		{
			type = "s";
		}
		else if (typeString.equals("double"))
		{
			type = "f";
		}
		else if (typeString.equals("void *"))
		{
			type = "p";
		}
		else if (typeString.equals("int *"))
		{
			type = "n";
		}
		else if (typeString.equals("char"))
		{
			type = "c";
		}
		else if (typeString.equals("long"))
		{
			type = "l";
		}
		else
		{
			type = "?";
		}
		return type;
	}

	private String setArgument(CParameterInfo parameter, int parameterOffset)
	{
		String parameterToMessage = "";
		parameterToMessage += "temp=&" + parameter.getUnitName() + ";\n";
		parameterToMessage += "msg.argument[" + parameterOffset + "]=temp;\n";
		// parameter.getParameterTypeString()+ " " + parameter.getUnitName()
		return parameterToMessage;
	}

	public void setFilter(Filter filter)
	{
		this.filter = filter;
	}

	public void setConcern(Concern concern)
	{
		if (concern.getPlatformRepresentation() instanceof CFile)
		{
			this.file = (CFile) concern.getPlatformRepresentation();
		}
		this.concern = concern;
	}

	public void setFile(CFile file)
	{
		this.file = file;
	}

	public void setMethod(CMethodInfo method)
	{
		this.method = method;
	}

	public void setElementNumber(int elementNumber)
	{
		this.elementNumber = elementNumber;
	}

	public String getBeforeAdvice()
	{
		return "";
	}

	public String getAfterAdvice()
	{
		return "";
	}

	/**
	 * @return returns the name of the filter
	 */
	public abstract String getType();

	/**
	 * @return true if advice has to be woven after pointuct
	 */
	public abstract boolean afterAdvice();

	/**
	 * @return true if advice has to be woven before pointuct
	 */
	public abstract boolean beforeAdvice();

	/**
	 * @return true if the filter redirects a message to another function
	 */
	public abstract boolean redirectMessage();

	/**
	 * @return returns a name of a header file (including .h) as a string that
	 *         contains a header where function or global definitions of
	 *         elements used in the advice are specified
	 */
	public abstract String headerFile();

	/**
	 * @return true if there are other headers needed to make this filter work
	 */
	public abstract boolean needsHeaderFiles();

}
