/*
 * Created on 23-aug-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.LAMA.MethodInfo;

/**
 * This strategy is called by the LowLevelInliner to do the actual code
 * generation.
 * 
 * @author Arjan
 */
public interface LowLevelInlineStrategy
{
	/**
	 * Called to indicate that the inlining starts.
	 * 
	 * @param filterSet The filterset
	 * @param method The method for which inlining is done
	 * @param argReferences
	 */
	public void startInline(FilterModuleOrder filterSet, MethodInfo method);

	/**
	 * Called when inlining is done.
	 */
	public void endInline();

	/**
	 * Called to indicate the start of a filterblock.
	 * 
	 * @param filter The filter
	 * @param jumpLabel The label of this filterblock
	 */
	public void startFilter(Filter filter, int jumpLabel);

	/**
	 * Called to indicate the end of a filterblock.
	 */
	public void endFilter();

	/**
	 * Called to indicate that a condition needs to be evaluated. Currently used
	 * only for filter module conditions
	 * 
	 * @param condition
	 */
	public void evalCondition(Condition condition, int jumpLabel);

	/**
	 * Called to indicate that a conditionexpression needs to be evaluated
	 * 
	 * @param condition The conditionexpression
	 */
	public void evalCondExpr(ConditionExpression condition);

	/**
	 * The start of the truebranch after a condition expression evaluation.
	 */
	public void beginTrueBranch();

	/**
	 * The end of the truebranch.
	 */
	public void endTrueBranch();

	/**
	 * The start of the falsebranch after a condition expression evaluation.
	 */
	public void beginFalseBranch();

	/**
	 * The end of the falsebranch.
	 */
	public void endFalseBranch();

	/**
	 * Called when a jump needs to be done to the given jumplabel. When the
	 * jumplabel is -1, this indicates a jump to the end of the filtercode.
	 * 
	 * @param jumpLabel
	 */
	public void jump(int jumpLabel);

	/**
	 * Called when the code for a certain filteraction needs to be generated.
	 * 
	 * @param state The executionstate corresponding with the filteraction.
	 */
	public void generateAction(ExecutionState state, List<String> resourceOps);
}
