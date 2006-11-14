package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

public class FilterModuleParameter extends DeclaredRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3840471205660574871L;

	public Object value;

	public FilterModuleParameterAST fmpAst;

	public int uniqueNumber;

	public FilterModuleParameter()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public FilterModuleParameter(FilterModuleParameterAST fmp, Object aValue, int number)
	{
		super();
		fmpAst = fmp;
		name = fmpAst.getName();
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

	public void setUniqueNumber(int inUniqueNumber)
	{
		uniqueNumber = inUniqueNumber;
	}
}
