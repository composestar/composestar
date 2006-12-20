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
import Composestar.Core.FIRE2.model.ExecutionLabels;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.FIRE2.util.queryengine.predicates.StateType;
import Composestar.Core.LAMA.MethodInfo;

public class LowLevelInliner
{
	private LowLevelInlineStrategy strategy;

	private StateType isCondExpr;

	private StateType isFilter;

	private HashMap filterMap;

	public LowLevelInliner(LowLevelInlineStrategy strategy)
	{
		this.strategy = strategy;

		initialize();
	}

	private void initialize()
	{
		isFilter = new StateType(FlowChartNames.FILTER_NODE);
		isCondExpr = new StateType(FlowChartNames.CONDITION_EXPRESSION_NODE);
	}

	public void inline(ExecutionModel model, FilterModule[] modules, MethodInfo method)
	{
		filterMap = new HashMap();

		Vector blocks = identifyFilters(model);

		inline(blocks, modules, method);
	}

	private void inline(Vector blocks, FilterModule[] modules, MethodInfo method)
	{
		strategy.startInline(modules, method, new String[0]);

		for (int i = 0; i < blocks.size(); i++)
		{
			inlineFilterBlock((FilterBlock) blocks.elementAt(i));
		}

		strategy.endInline();
	}

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
				strategy.generateAction(flowTrueExitState);
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

				strategy.generateAction(flowTrueExitState);
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
				strategy.generateAction(flowFalseExitState);
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
					strategy.generateAction(flowFalseExitState);
					generateJump(flowFalseExitState);
				}
				else
				{
					ExecutionState conditionExprState = filterElement.conditionExprState;
					FlowNode condExprFlowNode = conditionExprState.getFlowNode();

					strategy.evalCondExpr((ConditionExpression) condExprFlowNode.getRepositoryLink());
					strategy.beginTrueBranch();

					strategy.generateAction(flowTrueExitState);
					generateJump(flowTrueExitState);

					strategy.endTrueBranch();
					strategy.beginFalseBranch();

					strategy.generateAction(flowFalseExitState);
					generateJump(flowFalseExitState);

					strategy.endFalseBranch();
				}
			}
		}
	}

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

	private FilterBlock identifyFilterBlock(ExecutionState filterState)
	{
		FilterBlock filterBlock = new FilterBlock();
		filterBlock.filter = (Filter) filterState.getFlowNode().getRepositoryLink();
		identifyFilterElementBlocks(filterState, filterBlock);
		filterMap.put(filterState, filterBlock);
		return filterBlock;
	}

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

			if (transition.getLabel().equals(ExecutionLabels.CONDITION_EXPRESSION_TRUE))
			{
				block.flowTrueExitState = exitState;
			}
			else
			{
				block.flowFalseExitState = exitState;
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

	private boolean isExitState(ExecutionState state)
	{
		// exitstate is either a ConditionExpression or an Action state:
		return isCondExpr.isTrue(state) || state.getFlowNode().containsName(FlowChartNames.ACTION_NODE);
	}

	private class FilterBlock
	{
		public Filter filter;

		public Vector filterElements;

		public int label;

		public FilterBlock()
		{}
	}

	private class FilterElementBlock
	{
		public ExecutionState conditionExprState;

		public ExecutionState flowTrueExitState;

		public ExecutionState flowFalseExitState;

		public FilterElementBlock()
		{}
	}

}
