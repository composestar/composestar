package Composestar.Core.SANE;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * Contains the superimposition of one filter module. This consists of the
 * FilterModule instance and the optional Condition instance.
 * 
 * @author Arjan
 */
public class FilterModuleSuperImposition extends RepositoryEntity
{
	private static final long serialVersionUID = -7267957969500237784L;

	/**
	 * The filter module in the superimposition
	 */
	private FilterModuleReference filterModule;

	/**
	 * The condition under which this filter module is superimposed, or
	 * <code>null</code> if the filter module is superimposed unconditionally.
	 */
	private Condition condition;

	public FilterModuleSuperImposition()
	{}

	/**
	 * Creates an unconditional filter module superimposition.
	 * 
	 * @param fm The filter module that is superimposed.
	 */
	public FilterModuleSuperImposition(FilterModuleReference fm)
	{
		filterModule = fm;
	}

	/**
	 * Creates a filter module superimposition.
	 * 
	 * @param fm The filter module that is superimposed.
	 * @param cond The condition under which the filter module is superimposed.
	 */
	public FilterModuleSuperImposition(FilterModuleReference fm, Condition cond)
	{
		filterModule = fm;
		condition = cond;
	}

	/**
	 * @return the condition under which the filter module is superimposed, or
	 *         <code>null</code> if the filter module is superimposed
	 *         unconditionally.
	 */
	public Condition getCondition()
	{
		return condition;
	}

	/**
	 * @return the filterModule
	 */
	public FilterModuleReference getFilterModule()
	{
		return filterModule;
	}

}
