/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.Case;
import Composestar.Core.INLINE.model.ContextExpression;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Switch;
import Composestar.Core.INLINE.model.While;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;

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

	private static int lastMethodId;

	private int nextReturnActionId;

	private Switch onReturnInstructions;

	private ContextInstruction createActionStoreInstruction;

	private Label returnLabel;

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

	/**
	 * Indicates that the next jump to end should be ignored (for error action).
	 * FIXME replace this with conceptual change in FIRE
	 */
	private boolean noJumpEnd;

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
	public Block getInlineBlock()
	{
		if (empty)
		{
			return null;
		}
		else
		{
			return inlineBlock;
		}
	}

	/**
	 * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startInline(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule[],
	 *      Composestar.Core.LAMA.MethodInfo, java.lang.String[])
	 */
	public void startInline(FilterModule[] filterSet, MethodInfo method, String[] argReferences)
	{
		if (filterSetType == INPUT_FILTERS)
		{
			startInlineIF(filterSet, method, argReferences);
		}
		else
		{
			startInlineOF(filterSet, method, argReferences);
		}
	}

	/**
	 * The startinline method for inputfilters
	 * 
	 * @param filterSet
	 * @param method
	 * @param argReferences
	 */
	private void startInlineIF(FilterModule[] filterSet, MethodInfo method, String[] argReferences)
	{
		this.currentMethod = method;

		inlineBlock = new Block();
		blockStack = new Stack();
		labelTable = new Hashtable();
		currentLabelId = -1;

		nextReturnActionId = 0;
		onReturnInstructions = new Switch(new ContextExpression(ContextExpression.RETRIEVE_ACTION));
		returnLabel = new Label(9997);

		empty = true;

		// create checkinnercall context instruction:
		Block block = new Block();

		ContextInstruction checkInnercall = new ContextInstruction(ContextInstruction.CHECK_INNER_CALL,
				getMethodId(method), block);
		inlineBlock.addInstruction(checkInnercall);

		// create CreateJoinPointContext instruction:
		block.addInstruction(new ContextInstruction(ContextInstruction.CREATE_JOIN_POINT_CONTEXT));

		// create CreateActionStore instruction:
		createActionStoreInstruction = new ContextInstruction(ContextInstruction.CREATE_ACTION_STORE);
		block.addInstruction(createActionStoreInstruction);

		// set current block to inner block of checkInnercall instruction:
		currentBlock = block;
	}

	/**
	 * The startinline method for the outputfilters
	 * 
	 * @param filterSet
	 * @param method
	 * @param argReferences
	 */
	private void startInlineOF(FilterModule[] filterSet, MethodInfo method, String[] argReferences)
	{
		this.currentMethod = method;

		inlineBlock = new Block();
		blockStack = new Stack();
		labelTable = new Hashtable();
		currentLabelId = -1;

		nextReturnActionId = 0;
		onReturnInstructions = new Switch(new ContextExpression(ContextExpression.RETRIEVE_ACTION));
		returnLabel = new Label(9997);

		empty = true;

		// create CreateJoinPointContext instruction:
		inlineBlock.addInstruction(new ContextInstruction(ContextInstruction.CREATE_JOIN_POINT_CONTEXT));

		// create CreateActionStore instruction:
		createActionStoreInstruction = new ContextInstruction(ContextInstruction.CREATE_ACTION_STORE);
		inlineBlock.addInstruction(createActionStoreInstruction);

		// set current block to inner block of checkInnercall instruction:
		currentBlock = inlineBlock;
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
	{
		// check whether there are on return actions:
		if (!onReturnInstructions.hasCases())
		{
			// remove createActionStore instruction:
			createActionStoreInstruction.setType(ContextInstruction.REMOVED);

			// set jumpLabel of onReturnJump to end:
			returnLabel.setId(9998);
		}
		else
		{
			// add onReturnActions:
			Block block = new Block();
			block.addInstruction(onReturnInstructions);

			While whileInstruction = new While(new ContextExpression(ContextExpression.HAS_MORE_ACTIONS), block);
			whileInstruction.setLabel(returnLabel);

			currentBlock.addInstruction(whileInstruction);
		}

		// create RestoreJoinPointContext instruction:
		ContextInstruction restoreInstruction = new ContextInstruction(ContextInstruction.RESTORE_JOIN_POINT_CONTEXT);
		restoreInstruction.setLabel(new Label(9998));
		currentBlock.addInstruction(restoreInstruction);

		// create Return action:
		ContextInstruction returnInstruction = new ContextInstruction(ContextInstruction.RETURN_ACTION);
		currentBlock.addInstruction(returnInstruction);

		// create resetInnercall context instruction:
		ContextInstruction resetInnercall = new ContextInstruction(ContextInstruction.RESET_INNER_CALL);
		resetInnercall.setLabel(getLabel(9999));
		inlineBlock.addInstruction(resetInnercall);
	}

	/**
	 * The endline method for the outputfilters
	 */
	private void endInlineOF()
	{
		// check whether there are on return actions:
		if (!onReturnInstructions.hasCases())
		{
			// remove createActionStore instruction:
			createActionStoreInstruction.setType(ContextInstruction.REMOVED);

			// set jumpLabel of onReturnJump to end:
			returnLabel.setId(9998);
		}
		else
		{
			// add onReturnActions:
			Block block = new Block();
			block.addInstruction(onReturnInstructions);

			While whileInstruction = new While(new ContextExpression(ContextExpression.HAS_MORE_ACTIONS), block);
			whileInstruction.setLabel(returnLabel);

			currentBlock.addInstruction(whileInstruction);
		}

		// create RestoreJoinPointContext instruction:
		ContextInstruction restoreInstruction = new ContextInstruction(ContextInstruction.RESTORE_JOIN_POINT_CONTEXT);
		restoreInstruction.setLabel(new Label(9998));
		currentBlock.addInstruction(restoreInstruction);
	}

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
			label = returnLabel;

			if (noJumpEnd)
			{
				noJumpEnd = false;
				return;
			}
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
			instruction = new FilterAction("ContinueAction", state.getMessage(), getSubstitutedMessage(state));
			currentBlock.addInstruction(instruction);
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
		else if (node.containsName("AdviceAction"))
		{
			generateAdviceAction(state);
		}
		else if (node.containsName(FlowChartNames.ACCEPT_CALL_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getAcceptCallAction();
			generateCallAction(state, action);
		}
		else if (node.containsName(FlowChartNames.REJECT_CALL_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getRejectCallAction();
			generateCallAction(state, action);
		}
		else if (node.containsName(FlowChartNames.ACCEPT_RETURN_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getAcceptReturnAction();
			generateReturnAction(state, action);
		}
		else if (node.containsName(FlowChartNames.REJECT_RETURN_ACTION_NODE))
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action = filterType
					.getRejectReturnAction();
			generateReturnAction(state, action);
		}
	}

	private void generateCallAction(ExecutionState state,
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action)
	{
		Instruction instruction = new FilterAction(action.getName(), state.getMessage(), getSubstitutedMessage(state));
		empty = false;
		currentBlock.addInstruction(instruction);
	}

	private void generateReturnAction(ExecutionState state,
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction action)
	{
		int actionId = nextReturnActionId++;
		ContextInstruction storeInstruction = new ContextInstruction(ContextInstruction.STORE_ACTION, actionId);
		currentBlock.addInstruction(storeInstruction);

		Block block = new Block();

		Instruction instruction = new FilterAction(action.getName(), state.getMessage(), getSubstitutedMessage(state));
		empty = false;
		block.addInstruction(instruction);

		Case caseInstruction = new Case(actionId, block);
		onReturnInstructions.addCase(caseInstruction);

	}

	private void generateDispatchAction(ExecutionState state)
	{
		Message callMessage = getSubstitutedMessage(state);

		ContextInstruction innerCallContext = setInnerCallContext(callMessage);
		if (innerCallContext != null)
		{
			currentBlock.addInstruction(innerCallContext);
		}

		FilterAction action = new FilterAction("DispatchAction", state.getMessage(), callMessage);
		currentBlock.addInstruction(action);

		Target target = callMessage.getTarget();
		String selector = callMessage.getSelector();
		if (!Message.checkEquals(Message.INNER_TARGET, target)
				|| !Message.checkEquals(selector, builder.getCurrentSelector()))
		{
			empty = false;
		}
	}

	private void generateAdviceAction(ExecutionState state)
	{
		FlowNode flowNode = state.getFlowNode();
		if (flowNode.containsName(FlowChartNames.ACCEPT_CALL_ACTION_NODE)
				|| flowNode.containsName(FlowChartNames.REJECT_CALL_ACTION_NODE))
		{
			generateBeforeAction(state);
		}
		else
		{
			generateAfterAction(state);
		}
	}

	private void generateBeforeAction(ExecutionState state)
	{
		Message callMessage = getSubstitutedMessage(state);

		ContextInstruction innerCallContext = setInnerCallContext(callMessage);
		if (innerCallContext != null)
		{
			currentBlock.addInstruction(innerCallContext);
		}

		FilterAction action = new FilterAction("AdviceAction", state.getMessage(), callMessage);
		currentBlock.addInstruction(action);

		empty = false;
	}

	private void generateAfterAction(ExecutionState state)
	{
		Message callMessage = getSubstitutedMessage(state);

		int actionId = nextReturnActionId++;
		ContextInstruction storeInstruction = new ContextInstruction(ContextInstruction.STORE_ACTION, actionId);
		currentBlock.addInstruction(storeInstruction);

		Block block = new Block();

		ContextInstruction innerCallContext = setInnerCallContext(callMessage);
		if (innerCallContext != null)
		{
			block.addInstruction(innerCallContext);
		}

		FilterAction action = new FilterAction("AdviceAction", state.getMessage(), callMessage);
		block.addInstruction(action);

		Case caseInstruction = new Case(actionId, block);
		onReturnInstructions.addCase(caseInstruction);

		empty = false;
	}

	/**
	 * Checks whether the call is an innercall and whether the called method has
	 * inlined filters. Then the innercall filtercontext needs to be set.
	 * 
	 * @param state
	 */
	private ContextInstruction setInnerCallContext(Message callMessage)
	{
		if (filterSetType != INPUT_FILTERS)
		{
			return null;
		}

		if (Message.checkEquals(callMessage.getTarget(), Message.INNER_TARGET))
		{
			MethodInfo calledMethod;

			if (callMessage.getSelector().equals(currentMethod.name()))
			{
				calledMethod = currentMethod;
			}
			else
			{
				List parameterList = currentMethod.getParameters();
				String[] parameters = new String[parameterList.size()];
				for (int i = 0; i < parameterList.size(); i++)
				{
					ParameterInfo parameter = (ParameterInfo) parameterList.get(i);
					parameters[i] = parameter.parameterType().fullName();
				}

				calledMethod = currentMethod.parent().getMethod(callMessage.getSelector(), parameters);
			}

			// it is possible that a called method could not be found, SIGN
			// already has given a warning
			// or error for this
			if (calledMethod == null)
			{
				return null;
			}

			ContextInstruction contextInstruction = new ContextInstruction(ContextInstruction.SET_INNER_CALL,
					getMethodId(calledMethod));
			builder.addInnerCallCheckTask(contextInstruction);
			return contextInstruction;
		}
		else
		{
			return null;
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
		if (Message.checkEquals(dispTarget, Message.STAR_TARGET)) dispTarget = state.getMessage().getTarget();

		// get the dispatch selector:
		String dispSelector = state.getSubstitutionMessage().getSelector();
		if (Message.checkEquals(dispSelector, Message.STAR_SELECTOR)) dispSelector = 
			state.getMessage().getSelector();

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
