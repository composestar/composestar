package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;

public class ParameterizedInternal extends Internal
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2786468861419775999L;

	public String parameter;

	public ParameterizedInternalAST pinAst;

	public ParameterizedInternal()
	{
		super();
	}

	public ParameterizedInternal(ParameterizedInternalAST par_ast)
	{
		super();
		pinAst = par_ast;
		name = pinAst.getName();
		parameter = pinAst.getParameter();
	}

	public void setParameter(String aParameter)
	{
		parameter = aParameter;
	}

	public String getParameter()
	{
		return parameter;
	}

	/**
	 * gets the type from the parent fm
	 */
	public ConcernReference getType()
	{
		return type;
	}

	public void setType(ConcernReference ref)
	{
		type = ref;
	}

	public int getDescriptionLineNumber()
	{
		return pinAst.getDescriptionLineNumber();
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		pinAst.setDescriptionLineNumber(newLineNumber);
	}

	public String getDescriptionFileName()
	{
		return pinAst.getDescriptionFileName();
	}

	public void setDescriptionFileName(String newFileName)
	{
		pinAst.setDescriptionFileName(newFileName);
	}
}
