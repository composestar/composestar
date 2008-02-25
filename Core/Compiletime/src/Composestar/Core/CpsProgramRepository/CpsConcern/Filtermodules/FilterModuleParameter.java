package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Vector;

import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

public class FilterModuleParameter extends DeclaredRepositoryEntity
{
	private static final long serialVersionUID = 3840471205660574871L;

	public Vector value;

	public FilterModuleParameterAST fmpAst;

	public int uniqueNumber;

	public FilterModuleParameter()
	{
		super();
	}

	public FilterModuleParameter(FilterModuleParameterAST fmp, FilterModuleParameter aValue, int number)
	{
		super();
		fmpAst = fmp;
		descriptionFileName = fmpAst.getDescriptionFileName();
		descriptionLineNumber = fmpAst.getDescriptionLineNumber();
		name = fmpAst.getName();
		value = aValue.getValue();
		uniqueNumber = number;
	}

	public void setValue(Vector o)
	{
		value = o;
	}

	public Vector getValue()
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
