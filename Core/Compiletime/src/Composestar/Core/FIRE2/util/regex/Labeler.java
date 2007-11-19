/*
 * Created on 12-jun-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.List;

import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * Interface used by SECRET to label nodes in the FIRE execution model for
 * resource conflict detection.
 * 
 * @author Arjan de Roo
 */
public interface Labeler
{
	/**
	 * Identifier used to identify the location of the filter action execution
	 * in the resource operation list as returned by getResourceOperations().
	 * Following this prefix is the short name of the filter action that is
	 * being executed.
	 */
	public static final String FILTER_ACTION_PREFIX = "@";

	/**
	 * Return label sequences for the given execution state
	 * 
	 * @param transition
	 * @return
	 */
	LabelSequence getLabels(ExecutionTransition transition);

	/**
	 * Get a list of all operations for the given transition. It will ignore
	 * filter action labels of the associated flow node. Used by INLINER to get
	 * all non-filter action labels.
	 * 
	 * @param transition
	 * @return
	 */
	List<String> getResourceOperations(ExecutionTransition transition);

	/**
	 * Set the current resource identifier. getLabels() needs this information
	 * to determine which label sequences to return for the execution
	 * transition.
	 * 
	 * @param resource
	 */
	void setCurrentResource(Resource resource);

	/**
	 * Set the concern that is currently processed.
	 * 
	 * @param curConcern
	 */
	void setCurrentConcern(Concern curConcern);
}
