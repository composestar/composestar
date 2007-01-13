/*
 * Created on 23-aug-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.FIRE2.util.queryengine.predicates.StateType;
import Composestar.Core.LAMA.MethodInfo;

/**
 * The inlining engine which also uses lowlevel constructs like jumps. This
 * inlining engine translates the executionmodel of a filterset (for a given
 * selector) to calls to a LowLevelInlineStrategy object, that abstractly
 * resemble the code translation of the filterset. So it translates the
 * filterset language to a procedural language. The LowLevelInlineStrategy
 * object is responsible for the actual code generation.
 * 
 * @author Arjan
 */
public class LowLevelInliner
{
	/**
	 * The strategy that does the actual code generation.
	 */
	private LowLevelInlineStrategy strategy;

	private StateType isCondExpr;

	private StateType isFilter;

	private HashMap filterMap;

	/**
	 * The constructor
	 * 
	 * @param strategy The strategy that does the actual code generation.
	 */
	public LowLevelInliner(LowLevelInlineStrategy strategy)
	{
		this.strategy = strategy;

		initialize();
	}

	/**
	 * Initialization
	 */
	private void initialize()
	{
		isFilter = new StateType(FlowNode.FILTER_NODE);
		isCondExpr = new StateType(FlowNode.CONDITION_EXPRESSION_NODE);
	}

	/**
	 * Runs the inlining engine for the given ExecutionModel and methodInfo.
	 * 
	 * @param model
	 * @param modules
	 * @param method
	 */
	public void inline(ExecutionModel model, FilterModule[] modules, MethodInfo method)
	{
		filterMap = new HashMap();

		Vector blocks = identifyFilters(model);

		inline(blocks, modules, method);
	}

	/**
	 * Does the inlining for the given vector of filterblocks
	 * 
	 * @param blocks
	 * @param modules
	 * @param method
	 */
	private void inline(Vector blocks, FilterModule[] modules, MethodInfo method)
	{
		strategy.startInline(modules, method, new String[0]);

		for (int i = 0; i < blocks.size(); i++)
		{
			inlineFilterBlock((FilterBlock) blocks.elementAt(i));
		}

		strategy.endInline();
	}

	/**
	 * Inlines the given filterblock
	 * 
	 * @param filterBlock
	 */
	private void inlineFilterBlock(FilterBlock filterBlock)
	{
		strategy.startFilter(filterBlock.filter, filterBlock.label);

		Enumeration filterElements = filterBlock.filterElements.elements();

		if (filterElements.hasMoreElements())
		{
			inlineFilterElements(filterElements);
		}

		strategy.endFilter();
	}

	/**
	 * Inlines the filterelements in the given enumeration.
	 * 
	 * @param filterElements
	 */
	private void inlineFilterElements(Enumeration filterElements)
	{
		FilterElementBlock filterElement = (FilterElementBlock) filterElements.nextElement();

		ExecutionState flowFalseExitState = filterElement.flowFalseExitState;
		ExecutionState flowTrueExitState = filterElement.flowTrueExitState;

		if (flowFalseExitState == null)
		{
			// flueTrue can't be null if flowFalse is null

			if (isCondExpr.isTrue(flowTrueExitState))
			{
				// continue with next filter element:
				inlineFilterElements(filterElements);
			}
			else
			{
				strategy.generateAction(filterElement.flowTrueAction1);
				strategy.generateAction(filterElement.flowTrueAction2);
				generateJump(flowTrueExitState);
			}
		}
		else if (isCondExpr.isTrue(flowFalseExitState))
		{
			if (flowTrueExitState == null)
			{
				// continue with next filter element:
				inlineFilterElements(filterElements);
			}
			else if (isCondExpr.isTrue(flowTrueExitState))
			{
				// continue with next filter element:
				inlineFilterElements(filterElements);
			}
			else
			{
				ExecutionState conditionExprState = filterElement.conditionExprState;
				FlowNode condExprFlowNode = conditionExprState.getFlowNode();

				strategy.evalCondExpr((ConditionExpression) condExprFlowNode.getRepositoryLink());
				strategy.beginTrueBranch();

				strategy.generateAction(filterElement.flowTrueAction1);
				strategy.generateAction(filterElement.flowTrueAction2);
				generateJump(flowTrueExitState);

				strategy.endTrueBranch();
				strategy.beginFalseBranch();

				// do the inlining of the next filter elements:
				inlineFilterElements(filterElements);

				strategy.endFalseBranch();
			}
		}
		else
		{
			if (flowTrueExitState == null)
			{
				strategy.generateAction(filterElement.flowFalseAction1);
				strategy.generateAction(filterElement.flowFalseAction2);
				generateJump(flowFalseExitState);
			}
			else if (isCondExpr.isTrue(flowTrueExitState))
			{
				// not possible
			}
			else
			{
				if (flowFalseExitState.equals(flowTrueExitState))
				{
					strategy.generateAction(filterElement.flowFalseAction1);
					strategy.generateAction(filterElement.flowFalseAction2);
					generateJump(flowFalseExitState);
				}
				else
				{
					ExecutionState conditionExprState = filterElement.conditionExprState;
					FlowNode condExprFlowNode = conditionExprState.getFlowNode();

					strategy.evalCondExpr((ConditionExpression) condExprFlowNode.getRepositoryLink());
					strategy.beginTrueBranch();

					strategy.generateAction(filterElement.flowTrueAction1);
					strategy.generateAction(filterElement.flowTrueAction2);
					generateJump(flowTrueExitState);

					strategy.endTrueBranch();
					strategy.beginFalseBranch();

					strategy.generateAction(filterElement.flowFalseAction1);
					strategy.generateAction(filterElement.flowFalseAction2);
					generateJump(flowFalseExitState);

					strategy.endFalseBranch();
				}
			}
		}
	}

	/**
	 * Generates a jump to the next filter after the given state.
	 * 
	 * @param state
	 */
	private void generateJump(ExecutionState state)
	{
		ExecutionState currentState = state;
		while (currentState != null && !isFilter.isTrue(currentState))
		{
			currentState = getNextState(currentState);
		}

		if (currentState != null)
		{
			FilterBlock block = (FilterBlock) filterMap.get(currentState);
			strategy.jump(block.label);
		}
		else
		{
			strategy.jump(-1);
		}
	}

	/**
	 * Identifies all filterblocks in the given ExecutionModel.
	 * 
	 * @param model
	 * @return
	 */
	private Vector identifyFilters(ExecutionModel model)
	{
		ExecutionStateIterator iterator = new ExecutionStateIterator(model);
		ExecutionState state;
		int label = 0;
		Vector result = new Vector();
		while (iterator.hasNext())
		{
			state = (ExecutionState) iterator.next();
			if (isFilter.isTrue(state))
			{
				FilterBlock block = identifyFilterBlock(state);
				block.label = label++;
				result.add(block);
			}
		}

		return result;
	}

	/**
	 * Identifies the complete filterblock corresponding with the given
	 * filterstate.
	 * 
	 * @param filterState
	 * @return
	 */
	private FilterBlock identifyFilterBlock(ExecutionState filterState)
	{
		FilterBlock filterBlock = new FilterBlock();
		filterBlock.filter = (Filter) filterState.getFlowNode().getRepositoryLink();
		identifyFilterElementBlocks(filterState, filterBlock);
		filterMap.put(filterState, filterBlock);
		return filterBlock;
	}

	/**
	 * Identifies all filterelement blocks corresponding with the given filter.
	 * 
	 * @param filterState
	 * @param filterBlock
	 */
	private void identifyFilterElementBlocks(ExecutionState filterState, FilterBlock filterBlock)
	{
		ExecutionState nextState = getNextState(filterState);
		Vector result = new Vector();

		while (nextState != null)
		{
			FilterElementBlock block = identifyFilterElementBlock(nextState);
			result.add(block);

			if (isCondExpr.isTrue(block.flowTrueExitState))
			{
				nextState = block.flowTrueExitState;
			}
			else if (isCondExpr.isTrue(block.flowFalseExitState))
			{
				nextState = block.flowFalseExitState;
			}
			else
			{
				nextState = null;
			}
		}

		filterBlock.filterElements = result;
	}

	/**
	 * Identifies the complete filterelement block corresponding with the
	 * condition expression.
	 * 
	 * @param condExpr
	 * @return
	 */
	private FilterElementBlock identifyFilterElementBlock(ExecutionState condExpr)
	{
		FilterElementBlock block = new FilterElementBlock();
		block.conditionExprState = condExpr;

		Iterator outTransitions = condExpr.getOutTransitions();
		// enumeration has 1 or 2 elements
		while (outTransitions.hasNext())
		{
			ExecutionTransition transition = (ExecutionTransition) outTransitions.next();

			ExecutionState exitState = getExitState(transition.getEndState());

			if (transition.getLabel().equals(ExecutionTransition.CONDITION_EXPRESSION_TRUE))
			{
				block.flowTrueExitState = exitState;
				if (exitState.getFlowNode().containsName(FlowNode.ACTION_NODE))
				{
					block.flowTrueAction1 = exitState;
					block.flowTrueAction2 = getNextState(exitState);
				}
			}
			else
			{
				block.flowFalseExitState = exitState;
				if (exitState.getFlowNode().containsName(FlowNode.ACTION_NODE))
				{
					block.flowFalseAction1 = exitState;
					block.flowFalseAction2 = getNextState(exitState);
				}
			}
		}

		return block;
	}

	/**
	 * Returns the first next state of the given state, or null if there is
	 * none.
	 * 
	 * @param state
	 * @return
	 */
	private ExecutionState getNextState(ExecutionState state)
	{
		Iterator transitions = state.getOutTransitions();
		if (transitions.hasNext())
		{
			ExecutionTransition transition = (ExecutionTransition) transitions.next();
			return transition.getEndState();
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method gets the exitstate from the filterelement for one trace
	 * starting with the given state. This exitState is either an actionstate or
	 * the next ConditionExpressionState, marking the next filterelement.
	 * 
	 * @param state
	 * @return
	 */
	private ExecutionState getExitState(ExecutionState state)
	{
		ExecutionState currentState = state;

		while (!isExitState(currentState))
		{
			// get the next state:
			Iterator outTransitions = currentState.getOutTransitions();
			ExecutionTransition transition = (ExecutionTransition) outTransitions.next();
			currentState = transition.getEndState();
		}

		return currentState;
	}

	/**
	 * Indicates whether the given executionstate is an exitstate or not.
	 * 
	 * @param state
	 * @return
	 */
	private boolean isExitState(ExecutionState state)
	{
		// exitstate is either a ConditionExpression or an Action state:
		return isCondExpr.isTrue(state) || state.getFlowNode().containsName(FlowNode.ACTION_NODE);
	}

	/**
	 * The filter block
	 * 
	 * @author Arjan
	 */
	private class FilterBlock
	{
		/**
		 * The filter
		 */
		public Filter filter;

		/**
		 * A vector containing the filterelement blocks.
		 */
		public Vector filterElements;

		/**
		 * The label of the filterblock, to generate jumps
		 */
		public int label;
	}

	/**
	 * The filterelement block
	 * 
	 * @author Arjan
	 */
	private class FilterElementBlock
	{
		/**
		 * The executionstate corresponding with the conditionexpression of this
		 * filterelement.
		 */
		public ExecutionState conditionExprState;

		/**
		 * The exit(last) state of this filterelement when the
		 * conditionexpression was true.
		 */
		public ExecutionState flowTrueExitState;

		/**
		 * The first (on return) action when the conditionexpression was true.
		 */
		public ExecutionState flowTrueAction1;

		/**
		 * The second (on call) action when the conditionexpression was true.
		 */
		public ExecutionState flowTrueAction2;

		/**
		 * The exit(last) state of this filterelement when the
		 * conditionexpression was false.
		 */
		public ExecutionState flowFalseExitState;

		/**
		 * The first (on return) action when the conditionexpression was false.
		 */
		public ExecutionState flowFalseAction1;

		/**
		 * The second (on call) action when the conditionexpression was false.
		 */
		public ExecutionState flowFalseAction2;

	}

}
