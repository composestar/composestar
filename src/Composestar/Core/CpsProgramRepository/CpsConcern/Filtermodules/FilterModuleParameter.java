package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

public class FilterModuleParameter extends DeclaredRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3840471205660574871L;

	public Object value;

	public FilterModuleParameterAST fmp_ast;

	public int uniqueNumber;

	public FilterModuleParameter()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public FilterModuleParameter(FilterModuleParameterAST fmp, Object aValue, int number)
	{
		super();
		fmp_ast = fmp;
		name = fmp_ast.getName();
		value = ((FilterModuleParameter) aValue).getValue();
		uniqueNumber = number;
	}

	public void setValue(Object o)
	{
		value = o;
	}

	public Object getValue()
	{
		return value;
	}

	public int getUniqueNumber()
	{
		return uniqueNumber;
	}

	public void setUniqueNumber(int uniqueNumber)
	{
		this.uniqueNumber = uniqueNumber;
	}
}
