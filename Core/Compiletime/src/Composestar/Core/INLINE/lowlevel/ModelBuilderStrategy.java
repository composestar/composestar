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
import java.util.List;
import java.util.Map;
import java.util.Stack;

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.Filters.FilterAction.FlowBehavior;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.FILTH2.DefaultInnerDispatchNames;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterActionInstruction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.SECRET3.ConcernAnalysis;
import Composestar.Core.SECRET3.Conflict;
import Composestar.Core.SECRET3.FilterSetAnalysis;
import Composestar.Utils.StringUtils;

public class ModelBuilderStrategy implements LowLevelInlineStrategy
{
	/**
	 * The ModelBuilder
	 */
	private ModelBuilder builder;

	/**
	 * The filtersettype;
	 */
	private FilterDirection filterSetType;

	/**
	 * The FilterCode instance of the current inline.
	 */
	private FilterCode filterCode;

	/**
	 * The complete instructionblock of the current inline
	 */
	private Block inlineBlock;

	/**
	 * The instruction block of the current scope
	 */
	private Block currentBlock;

	/**
	 * Contains all outer scope blocks of the current scope, the closest on top.
	 */
	private Stack<Block> blockStack;

	/**
	 * Indicates the current condition branch.
	 */
	private Branch currentBranch;

	/**
	 * Hashtable containing a mapping from integer labelid's to the
	 * corresponding Label object.
	 */
	private Map<Integer, Label> labelTable;

	/**
	 * Indicates whether the instructionset of the current inline is empty or
	 * not. When it is empty, this indicates that the filters don't change the
	 * behaviour of the method(call) and so no inline needs to be done on the
	 * given method(call)
	 */
	private boolean empty;

	/**
	 * The current filterlabel.
	 */
	private int currentLabelId;

	private Label endLabel = new Label(-1);

	private BookKeepingMode bookKeepingMode;

	/**
	 * The concern analysis, used when bookkeepingmode is set to ConflictPaths.
	 * Will be null otherwise, will also be null when there are no conflicts
	 */
	private ConcernAnalysis concernAnalysis;

	/**
	 * List of conflicts for the current method. Will be set by startInline
	 */
	private List<Conflict> conflicts;

	private Filter currentFilter;

	private FilterElement currentFilterElement;

	/**
	 * The constructor
	 * 
	 * @param builder The modelbuilder.
	 * @param filterSetType Indicates whether this strategy is for inputfilters
	 *            (constant INPUT_FILTERS) or for outputfilters (constant
	 *            OUTPUT_FILTERS)
	 */
	public ModelBuilderStrategy(ModelBuilder builder, FilterDirection filterSetType, BookKeepingMode inBookKeepingMode)
	{
		this.builder = builder;
		this.filterSetType = filterSetType;
		bookKeepingMode = inBookKeepingMode;
	}

	public void setConcernAnalysis(ConcernAnalysis ca)
	{
		if (bookKeepingMode == BookKeepingMode.ConflictPaths)
		{
			concernAnalysis = ca;
		}
	}

	/**
	 * Returns the complete instructionblock of the current inline, or
	 * <code>null</code>when it is empty.
	 * 
	 * @return
	 */
	public FilterCode getFilterCode()
	{
		if (empty)
		{
			return null;
		}
		else
		{
			return filterCode;
		}
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startInline(Composestar.Core.FILTH.FilterModuleOrder,
	 *      Composestar.Core.LAMA.MethodInfo, java.lang.String[])
	 */
	public void startInline(List<ImposedFilterModule> filterSet, MethodInfo method)
	{
		conflicts = null;
		if (concernAnalysis != null)
		{
			FilterSetAnalysis fsa = concernAnalysis.getSelectedAnalysis(filterSetType);
			if (fsa != null)
			{
				conflicts = fsa.executionConflicts(new CpsSelectorMethodInfo(method));
				if (conflicts == null)
				{
					conflicts = fsa.executionConflicts(null);
				}
			}
		}

		filterCode = new FilterCode();

		inlineBlock = new Block();
		filterCode.setInstruction(inlineBlock);

		blockStack = new Stack<Block>();
		labelTable = new HashMap<Integer, Label>();
		currentLabelId = -1;

		empty = true;

		// set current block to inlineblock
		currentBlock = inlineBlock;

		// Check whether there are conditions to check before filter code can be
		// executed. This is the case when all filtermodules, except the last
		// default dispatch filter module, are conditional. Then we can check
		// all these conditions in advance and when they are all false, no
		// filtercode needs to be executed.
		boolean hasCheckConditions = true;
		for (ImposedFilterModule fmsi : filterSet)
		{
			if (DefaultInnerDispatchNames.FQN_FILTER_MODULE.equals(fmsi.getFilterModule().getFullyQualifiedName()))
			{
				continue;
			}
			hasCheckConditions = hasCheckConditions && fmsi.getCondition() != null;
		}

		if (hasCheckConditions)
		{
			for (ImposedFilterModule fmsi : filterSet)
			{
				if (fmsi.getCondition() != null)
				{
					filterCode.addCheckCondition(fmsi.getCondition());
				}
			}
		}
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endInline()
	 */
	public void endInline()
	{
		if (filterSetType == FilterDirection.INPUT)
		{
			endInlineIF();
		}
		else
		{
			endInlineOF();
		}
	}

	/**
	 * The endline method for the inputfilters
	 */
	private void endInlineIF()
	{}

	/**
	 * The endline method for the outputfilters
	 */
	private void endInlineOF()
	{}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startFilter(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter,
	 *      int)
	 */
	public void startFilter(Filter filter, int jumpLabel)
	{
		currentFilter = filter;
		Block filterBlock;

		currentLabelId = jumpLabel;

		filterBlock = new Block();
		filterBlock.setLabel(new Label(jumpLabel));
		currentBlock.addInstruction(filterBlock);

		pushBlock(filterBlock);
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endFilter()
	 */
	public void endFilter()
	{
		currentFilter = null;
		popBlock();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startFilterElement
	 * (Composestar.Core.CpsRepository2.FilterElements.FilterElement)
	 */
	public void startFilterElement(FilterElement matchedFilterElement)
	{
		currentFilterElement = matchedFilterElement;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endFilterElement
	 * ()
	 */
	public void endFilterElement()
	{
		currentFilterElement = null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#evalConditionMethod(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition,
	 *      int)
	 */
	public void evalConditionMethod(MethodReference condition, int jumpLabel)
	{
		Branch branch = new Branch(condition);
		branch.setLabel(new Label(jumpLabel));
		currentBlock.addInstruction(branch);
		currentLabelId = jumpLabel;

		currentBranch = branch;
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#evalMatchingExpr(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingExpression)
	 */
	public void evalMatchingExpr(MatchingExpression condition)
	{
		Branch branch = new Branch(condition);
		currentBlock.addInstruction(branch);

		currentBranch = branch;
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#beginTrueBranch()
	 */
	public void beginTrueBranch()
	{
		Block block = new Block();
		currentBranch.setTrueBlock(block);

		pushBlock(block);
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endTrueBranch()
	 */
	public void endTrueBranch()
	{
		popBlock();
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#beginFalseBranch()
	 */
	public void beginFalseBranch()
	{
		Block block = new Block();
		currentBranch.setFalseBlock(block);

		pushBlock(block);
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endFalseBranch()
	 */
	public void endFalseBranch()
	{
		popBlock();
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#jump(int)
	 */
	public void jump(int labelId)
	{
		Label label;
		if (labelId == -1)
		{
			label = endLabel;
		}
		else if (labelId != currentLabelId + 1)
		{
			label = getLabel(labelId);
			label.setUsed(true);
		}
		else
		{
			return;
		}

		Jump jump = new Jump(label);

		currentBlock.addInstruction(jump);
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#generateAction(Composestar.Core.FIRE2.model.ExecutionState)
	 */
	public void generateAction(ExecutionState state, Collection<CanonAssignment> filterArgs, List<String> resourceOps)
	{
		FlowNode node = state.getFlowNode();

		FilterAction action = (FilterAction) node.getRepositoryLink();

		if (FilterActionNames.CONTINUE_ACTION.equals(action.getName()))
		{
			// if ( node.containsName(FlowChartNames.ACCEPT_CALL_ACTION_NODE) ||
			// node.containsName(FlowChartNames.REJECT_CALL_ACTION_NODE)){
			// instruction = new FilterAction("ContinueAction",
			// state.getMessage(), getSubstitutedMessage(state));
			// currentBlock.addInstruction(instruction);
			// }
		}
		else if (FilterActionNames.DISPATCH_ACTION.equals(action.getName())
				|| FilterActionNames.SEND_ACTION.equals(action.getName()))
		{
			generateDispatchAction(action, state, resourceOps);
		}
		else if (action.getName().equals("SkipAction"))
		{
			// jump to end:
			jump(-1);
			return;
		}
		else if (node.containsName(FlowNode.ACCEPT_CALL_ACTION_NODE))
		{
			generateCallAction(state, action, filterArgs, resourceOps);
		}
		else if (node.containsName(FlowNode.REJECT_CALL_ACTION_NODE))
		{
			generateCallAction(state, action, filterArgs, resourceOps);
		}
		else if (node.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE))
		{
			generateReturnAction(state, action, filterArgs, resourceOps);
		}
		else if (node.containsName(FlowNode.REJECT_RETURN_ACTION_NODE))
		{
			generateReturnAction(state, action, filterArgs, resourceOps);
		}
	}

	/**
	 * Generates an action on call
	 * 
	 * @param state The state corresponding with the action
	 * @param action The action
	 */
	private void generateCallAction(ExecutionState state, FilterAction action, Collection<CanonAssignment> filterArgs,
			List<String> resourceOps)
	{
		FilterActionInstruction instruction =
				new FilterActionInstruction(action.getName(), state.getMessage(), filterArgs, true, action
						.getFlowBehavior() == FlowBehavior.RETURN);
		instruction.setCreateJPC(action.needsJoinPointContext());
		instruction.setFilter(currentFilter);
		instruction.setFilterElement(currentFilterElement);
		setBookKeeping(instruction, state, resourceOps);

		empty = false;
		currentBlock.addInstruction(instruction);
	}

	/**
	 * Generates an action on return
	 * 
	 * @param state The state corresponding with the action
	 * @param action The action
	 */
	private void generateReturnAction(ExecutionState state, FilterAction action,
			Collection<CanonAssignment> filterArgs, List<String> resourceOps)
	{
		FilterActionInstruction instruction =
				new FilterActionInstruction(action.getName(), state.getMessage(), filterArgs, false, action
						.getFlowBehavior() == FlowBehavior.RETURN);
		instruction.setCreateJPC(action.needsJoinPointContext());
		instruction.setFilter(currentFilter);
		instruction.setFilterElement(currentFilterElement);
		setBookKeeping(instruction, state, resourceOps);

		empty = false;
		currentBlock.addInstruction(instruction);
	}

	/**
	 * Generates the dispatch action. This action is treated seperately, because
	 * if only a dispatch is done to the inner method, the filtercode can be
	 * ignored.
	 * 
	 * @param state The state corresponding with the action
	 */
	private void generateDispatchAction(FilterAction action, ExecutionState state, List<String> resourceOps)
	{
		CpsMessage msg = state.getMessage();
		FilterActionInstruction instruction =
				new FilterActionInstruction(action.getName(), msg, new ArrayList<CanonAssignment>(), true, true);
		instruction.setCreateJPC(JoinPointContextArgument.UNUSED);
		instruction.setFilter(currentFilter);
		instruction.setFilterElement(currentFilterElement);
		setBookKeeping(instruction, state, resourceOps);
		currentBlock.addInstruction(instruction);

		CpsObject target = msg.getTarget();
		CpsSelector sel = msg.getSelector();
		if (!target.isInnerObject() || !sel.compatible(builder.getCurrentSelector()))
		{
			empty = false;
		}
	}

	/**
	 * Pushes an instruction block to the block stack (change to inner scope).
	 * 
	 * @param newBlock
	 */
	private void pushBlock(Block newBlock)
	{
		blockStack.push(currentBlock);
		currentBlock = newBlock;
	}

	/**
	 * Pops an instruction block from the block stack (change to outer scope).
	 */
	private void popBlock()
	{
		currentBlock = blockStack.pop();
	}

	/**
	 * Returns the label corresponding with the given labelId. If the label
	 * doesn't exist yet, it is created.
	 * 
	 * @param labelId
	 * @return
	 */
	private Label getLabel(int labelId)
	{
		Integer wrapper = labelId;
		if (labelTable.containsKey(wrapper))
		{
			return labelTable.get(wrapper);
		}
		else
		{
			Label label = new Label(labelId);
			labelTable.put(wrapper, label);
			return label;
		}
	}

	/**
	 * Sets the bookkeeping value of the given action.
	 * 
	 * @param forAction
	 */
	private void setBookKeeping(FilterActionInstruction forAction, ExecutionState state, List<String> resourceOps)
	{
		boolean val;
		switch (bookKeepingMode)
		{
			case Always:
				val = true;
				break;
			case ConflictPaths:
				// this is a very basic check, runtime conflict detection is
				// performed from the begin to the end of a join point, thefore
				// if the current method has conflicts associated with it all
				// filter actions should be monitored. runtime conflict
				// detection could be improved by just monitoring the
				// conflicting subpath, in that case this check will need to see
				// if the filteraction is in a trace of the conflict list.
				val = concernAnalysis != null && conflicts != null;
				break;
			default:
				val = false;
		}
		if (val)
		{
			if (resourceOps != null && resourceOps.size() > 0)
			{
				forAction.setResourceOperations(StringUtils.join(resourceOps, ";"));
			}
			forAction.setBookKeeping(true);
			filterCode.setBookKeeping(true);
		}
	}
}
