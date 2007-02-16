package Composestar.Core.SANE;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * Contains the superimposition of one filter module. This consists of the
 * FilterModule instance and the optional Condition instance.
 * 
 * @author Arjan
 */
public class FilterModuleSuperImposition extends RepositoryEntity
{
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
	 * @param filterModule The filter module that is superimposed.
	 */
	public FilterModuleSuperImposition(FilterModuleReference filterModule)
	{
		this.filterModule = filterModule;
	}

	/**
	 * Creates a filter module superimposition.
	 * 
	 * @param filterModule The filter module that is superimposed.
	 * @param condition The condition under which the filter module is
	 *            superimposed.
	 */
	public FilterModuleSuperImposition(FilterModuleReference filterModule, Condition condition)
	{
		this.filterModule = filterModule;
		this.condition = condition;
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
