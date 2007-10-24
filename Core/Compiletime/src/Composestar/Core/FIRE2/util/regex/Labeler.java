/*
 * Created on 12-jun-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

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
	 * Return label sequences for the given execution state
	 * 
	 * @param transition
	 * @return
	 */
	LabelSequence getLabels(ExecutionTransition transition);

	/**
	 * Set the current resource identifier. getLabels() needs this information
	 * to determine which label sequences to return for the execution
	 * transition.
	 * 
	 * @param resource
	 */
	void setCurrentResource(String resource);

	/**
	 * Set the concern that is currently processed.
	 * 
	 * @param curConcern
	 */
	void setCurrentConcern(Concern curConcern);
}
