/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.SANE.FilterModuleSuperImposition;

public class ModelBuilderStrategy implements LowLevelInlineStrategy
{
	public final static int INPUT_FILTERS = 1;

	public final static int OUTPUT_FILTERS = 2;

	/**
	 * The ModelBuilder
	 */
	private ModelBuilder builder;

	/**
	 * The filtersettype;
	 */
	private int filterSetType;

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
	private Stack blockStack;

	/**
	 * Indicates the current condition branch.
	 */
	private Branch currentBranch;

	/**
	 * Hashtable containing a mapping from integer labelid's to the
	 * corresponding Label object.
	 */
	private Hashtable labelTable;

	/**
	 * Hashtable containing a mapping from MethodInfo to integer id's
	 */
	private static Hashtable methodTable;

	/**
	 * The last generated methodid.
	 */
	private static int lastMethodId;

	/**
	 * Indicates whether the instructionset of the current inline is empty or
	 * not. When it is empty, this indicates that the filters don't change the
	 * behaviour of the method(call) and so no inline needs to be done on the
	 * given method(call)
	 */
	private boolean empty;

	/**
	 * The method(call) currently being inlined.
	 */
	private MethodInfo currentMethod;

	/**
	 * The current filterlabel.
	 */
	private int currentLabelId;

	private Label endLabel = new Label(-1);

	/**
	 * The constructor
	 * 
	 * @param builder The modelbuilder.
	 * @param filterSetType Indicates whether this strategy is for inputfilters
	 *            (constant INPUT_FILTERS) or for outputfilters (constant
	 *            OUTPUT_FILTERS)
	 */
	public ModelBuilderStrategy(ModelBuilder builder, int filterSetType)
	{
		this.builder = builder;
		this.filterSetType = filterSetType;
		methodTable = new Hashtable();
		lastMethodId = 0;

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
	public void startInline(FilterModuleOrder filterSet, MethodInfo method, String[] argReferences)
	{
		filterCode = new FilterCode();

		this.currentMethod = method;

		inlineBlock = new Block();
		filterCode.setInstruction(inlineBlock);

		blockStack = new Stack();
		labelTable = new Hashtable();
		currentLabelId = -1;

		empty = true;

		// set current block to inlineblock
		currentBlock = inlineBlock;

		// Check whether there are conditions to check before filter code can be
		// executed. This is the case when all filtermodules, except the last
		// default dispatch filter module, are conditional. Then we can check
		// all these conditions in advance and when they are all false, no
		// filtercode needs to be executed.
		List list = filterSet.filterModuleSIList();
		boolean hasCheckConditions = true;
		for (int i = 0; i < list.size() - 1; i++)
		{
			FilterModuleSuperImposition fmsi = (FilterModuleSuperImposition) list.get(i);
			hasCheckConditions = hasCheckConditions && (fmsi.getCondition() != null);
		}

		if (hasCheckConditions)
		{
			for (int i = 0; i < list.size() - 1; i++)
			{
				FilterModuleSuperImposition fmsi = (FilterModuleSuperImposition) list.get(i);
				filterCode.addCheckCondition(fmsi.getCondition());
			}
		}
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endInline()
	 */
	public void endInline()
	{
		if (filterSetType == INPUT_FILTERS)
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
		popBlock();
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#evalCondition(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition)
	 */
	public void evalCondition(Condition condition)
	{
		System.out.println("###########EVAL CONDITION################");
		Branch branch = new Branch(condition);
		this.currentBlock.addInstruction(branch);

		this.currentBranch = branch;
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#evalCondExpr(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression)
	 */
	public void evalCondExpr(ConditionExpression condition)
	{
		Branch branch = new Branch(condition);
		this.currentBlock.addInstruction(branch);

		this.currentBranch = branch;
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#beginTrueBranch()
	 */
	public void beginTrueBranch()
	{
		Block block = new Block();
		this.currentBranch.setTrueBlock(block);

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
		this.currentBranch.setFalseBlock(block);

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
		}
		else
		{
			return;
		}

		Jump jump = new Jump(label);

		this.currentBlock.addInstruction(jump);
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#generateAction(Composestar.Core.FIRE2.model.ExecutionState)
	 */
	public void generateAction(ExecutionState state)
	{
		Instruction instruction;

		FlowNode node = state.getFlowNode();

		Filter filter = (Filter) node.getRepositoryLink();
		FilterType filterType = filter.getFilterType();

		if (node.containsName("ContinueAction"))
		{
			// if ( node.containsName(FlowChartNames.ACCEPT_CALL_ACTION_NODE) ||
			// node.containsName(FlowChartNames.REJECT_CALL_ACTION_NODE)){
			// instruction = new FilterAction("ContinueAction",
			// state.getMessage(), getSubstitutedMessage(state));
			// currentBlock.addInstruction(instruction);
			// }
		}
		else if (node.containsName("DispatchAction"))
		{
			generateDispatchAction(state);
		}
		else if (node.containsName("SkipAction"))
		{
			// jump to end:
			jump(-1);
			return;
		}
		else if (node.containsName(FlowNode.ACCEPT_CALL_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getAcceptCallAction();
			generateCallAction(state, action);
		}
		else if (node.containsName(FlowNode.REJECT_CALL_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getRejectCallAction();
			generateCallAction(state, action);
		}
		else if (node.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getAcceptReturnAction();
			generateReturnAction(state, action);
		}
		else if (node.containsName(FlowNode.REJECT_RETURN_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getRejectReturnAction();
			generateReturnAction(state, action);
		}
	}

	/**
	 * Generates an action on call
	 * 
	 * @param state The state corresponding with the action
	 * @param action The action
	 */
	private void generateCallAction(ExecutionState state,
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action)
	{
		Instruction instruction = new FilterAction(
				action.getName(),
				state.getMessage(),
				getSubstitutedMessage(state),
				true,
				action.getFlowBehaviour() == Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction.FLOW_RETURN);

		empty = false;
		currentBlock.addInstruction(instruction);
	}

	/**
	 * Generates an action on return
	 * 
	 * @param state The state corresponding with the action
	 * @param action The action
	 */
	private void generateReturnAction(ExecutionState state,
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action)
	{
		Instruction instruction = new FilterAction(
				action.getName(),
				state.getMessage(),
				getSubstitutedMessage(state),
				false,
				action.getFlowBehaviour() == Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction.FLOW_RETURN);

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
	private void generateDispatchAction(ExecutionState state)
	{
		Message callMessage = getSubstitutedMessage(state);

		FilterAction action = new FilterAction("DispatchAction", state.getMessage(), callMessage, true, true);
		currentBlock.addInstruction(action);

		Target target = callMessage.getTarget();
		String selector = callMessage.getSelector();
		if (!Message.checkEquals(Message.INNER_TARGET, target)
				|| !Message.checkEquals(selector, builder.getCurrentSelector()))
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
		this.blockStack.push(this.currentBlock);
		this.currentBlock = newBlock;
	}

	/**
	 * Pops an instruction block from the block stack (change to outer scope).
	 */
	private void popBlock()
	{
		currentBlock = (Block) blockStack.pop();
	}

	/**
	 * Creates the message that is called out of the original message and the
	 * substitution selector and target
	 * 
	 * @param state
	 * @return
	 */
	private Message getSubstitutedMessage(ExecutionState state)
	{
		// get the dispatch target:
		Target dispTarget = state.getSubstitutionMessage().getTarget();
		if (Message.checkEquals(dispTarget, Message.STAR_TARGET))
		{
			dispTarget = state.getMessage().getTarget();
		}

		// get the dispatch selector:
		String dispSelector = state.getSubstitutionMessage().getSelector();
		if (Message.checkEquals(dispSelector, Message.STAR_SELECTOR))
		{
			dispSelector = state.getMessage().getSelector();
		}

		return new Message(dispTarget, dispSelector);
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
		Integer wrapper = new Integer(labelId);
		if (labelTable.containsKey(wrapper))
		{
			return (Label) labelTable.get(wrapper);
		}
		else
		{
			Label label = new Label(labelId);
			labelTable.put(wrapper, label);
			return label;
		}
	}

	/**
	 * Returns the methodid corresponding with the given MethodInfo.
	 * 
	 * @param method
	 * @return
	 */
	public static int getMethodId(MethodInfo method)
	{
		if (method == null)
		{
			return -1;
		}

		Integer id = (Integer) methodTable.get(method);
		if (id == null)
		{
			id = new Integer(lastMethodId++);
			methodTable.put(method, id);
		}

		return id.intValue();
	}
}
