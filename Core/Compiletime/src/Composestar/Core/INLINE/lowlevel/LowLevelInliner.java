/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.CanonAssignmentImpl;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.FIRE2.util.queryengine.predicates.StateType;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SECRET3.SECRETResources;

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

	private StateType isFilterElement;

	private StateType isFilter;

	private StateType isFMCondition;

	private Map<ExecutionState, TopLevelBlock> jumpMap;

	private List<String> resourceOperations;

	private Labeler labeler;

	private Set<String> excludeResources;

	/**
	 * The constructor
	 * 
	 * @param inlineStrategy The strategy that does the actual code generation.
	 */
	public LowLevelInliner(LowLevelInlineStrategy inlineStrategy, CommonResources resources)
	{
		strategy = inlineStrategy;
		SECRETResources secretresc = resources.getResourceManager(SECRETResources.class);
		if (secretresc != null)
		{
			labeler = secretresc.getLabeler();
		}
		initialize();
	}

	/**
	 * Initialization
	 */
	private void initialize()
	{
		isFilter = new StateType(FlowNode.FILTER_NODE);
		isFilterElement = new StateType(FlowNode.FILTER_ELEMENT_NODE);
		isFMCondition = new StateType(FlowNode.FM_CONDITION_NODE);

		excludeResources = new HashSet<String>();
		excludeResources.add("message");
		excludeResources.add("message.target");
		excludeResources.add("message.sender");
		excludeResources.add("message.self");
		excludeResources.add("message.server");
		excludeResources.add("message.selector");
	}

	/**
	 * Runs the inlining engine for the given ExecutionModel and methodInfo.
	 * 
	 * @param model
	 * @param filterSet
	 * @param method
	 */
	public void inline(ExecutionModel model, List<ImposedFilterModule> filterSet, MethodInfo method)

	{
		jumpMap = new HashMap<ExecutionState, TopLevelBlock>();

		List<TopLevelBlock> blocks = identifyTopLevelElements(model);

		inline(blocks, filterSet, method);
	}

	/**
	 * Does the inlining for the given vector of filterblocks
	 * 
	 * @param blocks
	 * @param filterSet
	 * @param method
	 */
	private void inline(List<TopLevelBlock> blocks, List<ImposedFilterModule> filterSet, MethodInfo method)
	{
		strategy.startInline(filterSet, method);

		for (TopLevelBlock block : blocks)
		{
			if (block instanceof FilterBlock)
			{
				inlineFilterBlock((FilterBlock) block);
			}
			else if (block instanceof FilterModuleCondition)
			{
				inlineFilterModuleCondition((FilterModuleCondition) block);
			}
			else
			{
				// should not happen:
				throw new RuntimeException("Unknown top level block");
			}
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

		Iterator<FilterElementBlock> filterElements = filterBlock.filterElements.iterator();

		if (filterElements.hasNext())
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
	private void inlineFilterElements(Iterator<FilterElementBlock> filterElements)
	{
		FilterElementBlock filterElement = filterElements.next();

		FilterElement matchedFilterElement = null;
		if (filterElement.filterElementState.getFlowNode().getRepositoryLink() instanceof FilterElement)
		{
			matchedFilterElement = (FilterElement) filterElement.filterElementState.getFlowNode().getRepositoryLink();
		}

		ExecutionState flowFalseExitState = filterElement.falseExit;
		ExecutionState flowTrueExitState = filterElement.trueExit;

		// the filter argument
		Map<String, CanonAssignment> filterArgs = new HashMap<String, CanonAssignment>();
		addFilterArguments(filterElement.filterElementState, filterArgs, false);
		Map<String, CanonAssignment> filterArgsFE = new HashMap<String, CanonAssignment>(filterArgs);
		addFilterArguments(filterElement.filterElementState, filterArgsFE, true);

		if (flowFalseExitState == null)
		{
			// flowTrue can't be null if flowFalse is null

			if (isFilterElement.isTrue(flowTrueExitState))
			{
				// continue with next filter element:
				inlineFilterElements(filterElements);
			}
			else
			{
				strategy.startFilterElement(matchedFilterElement);
				strategy.generateAction(filterElement.trueActionReturn, filterArgsFE.values(),
						filterElement.trueRescOpsReturn);
				strategy.generateAction(filterElement.trueActionCall, filterArgsFE.values(),
						filterElement.trueRescOpsCall);
				strategy.endFilterElement();
				generateJump(flowTrueExitState);
			}
		}
		else if (isFilterElement.isTrue(flowFalseExitState))
		{
			if (flowTrueExitState == null)
			{
				// continue with next filter element:
				inlineFilterElements(filterElements);
			}
			else if (isFilterElement.isTrue(flowTrueExitState))
			{
				// continue with next filter element:
				inlineFilterElements(filterElements);
			}
			else
			{
				strategy.evalMatchingExpr(filterElement.expression);
				strategy.beginTrueBranch();

				strategy.startFilterElement(matchedFilterElement);
				strategy.generateAction(filterElement.trueActionReturn, filterArgsFE.values(),
						filterElement.trueRescOpsReturn);
				strategy.generateAction(filterElement.trueActionCall, filterArgsFE.values(),
						filterElement.trueRescOpsCall);
				strategy.endFilterElement();
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
				strategy.generateAction(filterElement.falseActionReturn, filterArgs.values(),
						filterElement.falseRescOpsReturn);
				strategy.generateAction(filterElement.falseActionCall, filterArgs.values(),
						filterElement.falseRescOpsCall);
				generateJump(flowFalseExitState);
			}
			else if (isFilterElement.isTrue(flowTrueExitState))
			{
				// not possible
			}
			else
			{
				if (flowFalseExitState.equals(flowTrueExitState))
				{
					strategy.generateAction(filterElement.falseActionReturn, filterArgs.values(),
							filterElement.falseRescOpsReturn);
					strategy.generateAction(filterElement.falseActionCall, filterArgs.values(),
							filterElement.falseRescOpsCall);
					generateJump(flowFalseExitState);
				}
				else
				{
					strategy.evalMatchingExpr(filterElement.expression);
					strategy.beginTrueBranch();

					strategy.startFilterElement(matchedFilterElement);
					strategy.generateAction(filterElement.trueActionReturn, filterArgsFE.values(),
							filterElement.trueRescOpsReturn);
					strategy.generateAction(filterElement.trueActionCall, filterArgsFE.values(),
							filterElement.trueRescOpsCall);
					strategy.endFilterElement();
					generateJump(flowTrueExitState);

					strategy.endTrueBranch();
					strategy.beginFalseBranch();

					strategy.generateAction(filterElement.falseActionReturn, filterArgs.values(),
							filterElement.falseRescOpsReturn);
					strategy.generateAction(filterElement.falseActionCall, filterArgs.values(),
							filterElement.falseRescOpsCall);
					generateJump(flowFalseExitState);

					strategy.endFalseBranch();
				}
			}
		}
	}

	/**
	 * Collect the filter arguments
	 * 
	 * @param feState
	 * @param result
	 * @param fromFe If true use the argument from filter element, otherwise use
	 *            the arguments from the filter
	 */
	private void addFilterArguments(ExecutionState feState, Map<String, CanonAssignment> result, boolean fromFe)
	{
		if (!(feState.getFlowNode().getRepositoryLink() instanceof FilterElement))
		{
			// TODO: error
			return;
		}
		CpsMessage message = feState.getMessage();
		FilterElement fe = (FilterElement) feState.getFlowNode().getRepositoryLink();
		Collection<CanonAssignment> assignments = null;
		if (fromFe)
		{
			assignments = fe.getAssignments();
		}
		else
		{
			RepositoryEntity re = fe.getOwner();
			while (re != null)
			{
				if (re instanceof Filter)
				{
					assignments = ((Filter) re).getArguments();
					break;
				}
				re = re.getOwner();
			}
		}
		if (assignments == null)
		{
			// TODO: error
			return;
		}

		for (CanonAssignment ca : assignments)
		{
			if (PropertyPrefix.FILTER != ca.getProperty().getPrefix())
			{
				continue;
			}
			CpsVariable value = ca.getValue();
			if (value instanceof CanonProperty)
			{
				CanonProperty prop = (CanonProperty) value;
				if (PropertyNames.INNER.equals(prop.getName()))
				{
					value = message.getInner();
				}
				else if (PropertyPrefix.MESSAGE == prop.getPrefix())
				{
					value = message.getProperty(prop.getBaseName());
				}
				else if (PropertyPrefix.FILTER == prop.getPrefix())
				{
					CanonAssignment calt = result.get(prop.getName());
					if (calt != null)
					{
						value = calt.getValue();
					}
					else
					{
						value = null;
					}
				}
				if (value != ca.getValue())
				{
					// duplicate the canon assignment with the real value
					CanonAssignment corig = ca;
					ca = new CanonAssignmentImpl();
					ca.setSourceInformation(corig.getSourceInformation());
					ca.setProperty(corig.getProperty());
					ca.setValue(value);
					ca.setOwner(corig.getOwner());
				}
			}
			if (value != null)
			{
				result.put(ca.getProperty().getName(), ca);
			}
			else
			{
				// TODO error
			}
		}
	}

	private void inlineFilterModuleCondition(FilterModuleCondition fmCond)
	{
		ImposedFilterModule ifm =
				(ImposedFilterModule) fmCond.filterModuleConditionState.getFlowNode().getRepositoryLink();

		strategy.evalConditionMethod(ifm.getCondition(), fmCond.label);

		// True branch
		strategy.beginTrueBranch();
		generateJump(fmCond.trueExit);
		strategy.endTrueBranch();

		// False branch
		strategy.beginFalseBranch();
		generateJump(fmCond.falseExit);
		strategy.endFalseBranch();
	}

	/**
	 * Generates a jump to the next filter after the given state.
	 * 
	 * @param state
	 */
	private void generateJump(ExecutionState state)
	{
		ExecutionState currentState = state;
		while (currentState != null && !(isFilter.isTrue(currentState) || isFMCondition.isTrue(currentState)))
		{
			currentState = getNextState(currentState);
		}

		if (currentState != null)
		{
			TopLevelBlock block = jumpMap.get(currentState);
			strategy.jump(block.label);
		}
		else
		{
			strategy.jump(-1);
		}
	}

	/**
	 * Identifies all filterblocks and filter module conditions in the given
	 * ExecutionModel.
	 * 
	 * @param model
	 * @return
	 */
	private List<TopLevelBlock> identifyTopLevelElements(ExecutionModel model)
	{
		ExecutionStateIterator iterator = new ExecutionStateIterator(model);
		ExecutionState state;
		int label = 0;
		List<TopLevelBlock> result = new ArrayList<TopLevelBlock>();
		resourceOperations = null;
		while (iterator.hasNext())
		{
			state = iterator.next();
			if (isFilter.isTrue(state))
			{
				FilterBlock block = identifyFilterBlock(state);
				block.label = label++;
				result.add(block);
				jumpMap.put(state, block);
			}
			else if (isFMCondition.isTrue(state))
			{
				FilterModuleCondition fmCond = identifyFilterModuleCondition(state);
				fmCond.label = label++;
				result.add(fmCond);
				jumpMap.put(state, fmCond);
			}
		}

		return result;
	}

	/**
	 * Identifies all FilterModuleBlocks in the given ExecutionModel.
	 * 
	 * @param model
	 * @return
	 */
	private FilterModuleCondition identifyFilterModuleCondition(ExecutionState state)
	{
		FilterModuleCondition fmCond = new FilterModuleCondition();

		fmCond.filterModuleConditionState = state;

		// Find true-exit and false-exit
		for (ExecutionTransition transition : state.getOutTransitionsEx())
		{
			if (transition.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
			{
				fmCond.trueExit = transition.getEndState();
			}
			else if (transition.getFlowTransition().getType() == FlowTransition.FLOW_FALSE_TRANSITION)
			{
				fmCond.falseExit = transition.getEndState();
			}
		}

		return fmCond;
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
		List<FilterElementBlock> result = new ArrayList<FilterElementBlock>();

		while (nextState != null)
		{
			FilterElementBlock block = identifyFilterElementBlock(nextState);
			result.add(block);

			if (isFilterElement.isTrue(block.trueExit))
			{
				nextState = block.trueExit;
			}
			else if (isFilterElement.isTrue(block.falseExit))
			{
				nextState = block.falseExit;
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
	 * @param filterElementState
	 * @return
	 */
	private FilterElementBlock identifyFilterElementBlock(ExecutionState filterElementState)
	{
		FilterElementBlock block = new FilterElementBlock();

		block.filterElementState = filterElementState;

		Set<ExecutionState> exitStates = new HashSet<ExecutionState>();
		getExitStates(filterElementState, exitStates);

		assert exitStates.size() <= 2 : "Filter element can not have more than 2 disctinct exit state";

		for (ExecutionState state : exitStates)
		{
			ExecutionState callAction = null;
			ExecutionState returnAction = null;

			if (state.getFlowNode().containsName(FlowNode.FILTER_ACTION_NODE))
			{
				// TODO assumes return-action, call-action; but does not
				// explicitly validate it
				returnAction = state;
				callAction = getNextState(state);
				if (!state.getFlowNode().containsName(FlowNode.FILTER_ACTION_NODE))
				{
					callAction = null;
				}
			}

			if (state.getFlowNode().containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE)
					|| state.getFlowNode().containsName(FlowNode.ACCEPT_CALL_ACTION_NODE))
			{
				// accepting exit state
				block.trueExit = state;
				block.trueActionCall = callAction;
				block.trueActionReturn = returnAction;
				// TODO: resource operations
			}
			else
			{
				// must be rejecting exit state
				block.falseExit = state;
				block.falseActionCall = callAction;
				block.falseActionReturn = returnAction;
				// TODO: resource operations ??
			}
		}

		if (exitStates.size() >= 2)
		{
			// reconstruct the expression making various things constant
			block.expression = ExpressionResolver.createExpression(filterElementState, block.trueExit, block.falseExit);
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
		List<ExecutionTransition> transitions = state.getOutTransitionsEx();
		if (transitions.size() > 0)
		{
			ExecutionTransition transition = transitions.get(0);
			if (labeler != null && resourceOperations != null)
			{
				resourceOperations.addAll(labeler.getResourceOperations(transition, excludeResources));
			}
			return transition.getEndState();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Find all exit states
	 * 
	 * @param state
	 * @param result exit states will be saved here
	 */
	private void getExitStates(ExecutionState state, Set<ExecutionState> result)
	{
		for (ExecutionTransition trans : state.getOutTransitionsEx())
		{
			ExecutionState os = trans.getEndState();
			if (isExitState(os))
			{
				result.add(os);
			}
			else
			{
				getExitStates(os, result);
			}
		}
	}

	/**
	 * @param state
	 * @return True then this state left the previous filter element scope and
	 *         entered either a new filter element scope or returned to the
	 *         filter scope.
	 */
	private boolean isExitState(ExecutionState state)
	{
		return state.getFlowNode().containsName(FlowNode.FILTER_ACTION_NODE) || isFilterElement.isTrue(state);
	}

	/**
	 * The filter block
	 * 
	 * @author Arjan
	 */
	private class FilterBlock extends TopLevelBlock
	{
		/**
		 * The filter
		 */
		public Filter filter;

		/**
		 * A vector containing the filterelement blocks.
		 */
		public List<FilterElementBlock> filterElements;
	}

	/**
	 * The filterelement block
	 * 
	 * @author Arjan
	 */
	private static class FilterElementBlock
	{
		/**
		 * The compact branching expression
		 */
		public MatchingExpression expression;

		/**
		 * The executionstate corresponding with the conditionexpression of this
		 * filterelement.
		 */
		public ExecutionState filterElementState;

		/**
		 * The exit(last) state of this filterelement when the
		 * conditionexpression was true.
		 */
		public ExecutionState trueExit;

		/**
		 * The first (on return) action when the conditionexpression was true.
		 */
		public ExecutionState trueActionReturn;

		/**
		 * The second (on call) action when the conditionexpression was true.
		 */
		public ExecutionState trueActionCall;

		public List<String> trueRescOpsReturn;

		public List<String> trueRescOpsCall;

		/**
		 * The exit(last) state of this filterelement when the
		 * conditionexpression was false.
		 */
		public ExecutionState falseExit;

		/**
		 * The first (on return) action when the conditionexpression was false.
		 */
		public ExecutionState falseActionReturn;

		/**
		 * The second (on call) action when the conditionexpression was false.
		 */
		public ExecutionState falseActionCall;

		public List<String> falseRescOpsReturn;

		public List<String> falseRescOpsCall;
	}

	private static class FilterModuleCondition extends TopLevelBlock
	{
		/**
		 * The execution state corresponding with the filter module condition.
		 */
		private ExecutionState filterModuleConditionState;

		/**
		 * The true exit state.
		 */
		private ExecutionState trueExit;

		/**
		 * The false exit state.
		 */
		private ExecutionState falseExit;
	}

	private static class TopLevelBlock
	{
		/**
		 * The label of the top level block, to generate jumps.
		 */
		public int label;
	}
}
