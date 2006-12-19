package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;

public class ParameterizedInternalAST extends InternalAST
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6521953901023131122L;

	public String parameter;

	public ParameterizedInternalAST()
	{
		super();
	}

	public ParameterizedInternalAST(String aParameter)
	{
		super();
		parameter = aParameter;
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
	 * must not be called at all, because it is an AST class
	 */
	public ConcernReference getType()
	{
		return null;
	}
}
