package Composestar.DotNET2.TYM.RepositoryEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.RepositoryImplementation.DataStore;

import composestar.dotNET2.tym.entities.AndCondition;
import composestar.dotNET2.tym.entities.ConditionLiteral;
import composestar.dotNET2.tym.entities.FalseCondition;
import composestar.dotNET2.tym.entities.InlineInstruction;
import composestar.dotNET2.tym.entities.JumpInstruction;
import composestar.dotNET2.tym.entities.NotCondition;
import composestar.dotNET2.tym.entities.OrCondition;
import composestar.dotNET2.tym.entities.TrueCondition;

class InstructionTranslator implements Visitor
{
	private Map<String, String> fullNameMap = new HashMap<String, String>();

	public InstructionTranslator(DataStore dataStore)
	{
		Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction filterAction;

		Iterator<Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction> it = dataStore
				.getAllInstancesOf(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction.class);

		while (it.hasNext())
		{
			filterAction = it.next();
			fullNameMap.put(filterAction.getName(), filterAction.getFullName());
		}
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
	 */
	public Object visitFilterCode(FilterCode filterCode)
	{
		composestar.dotNET2.tym.entities.FilterCode weaveFilterCode = composestar.dotNET2.tym.entities.FilterCode.Factory
				.newInstance();

		weaveFilterCode.setBookKeeping(filterCode.getBookKeeping());

		// Instructions
		InlineInstruction instruction = (InlineInstruction) filterCode.getInstruction().accept(this);
		weaveFilterCode.setInstructions(instruction);

		// Check conditions
		Iterator<Condition> condIter = filterCode.getCheckConditions();
		composestar.dotNET2.tym.entities.ConditionExpression condExpr = null;
		composestar.dotNET2.tym.entities.ConditionExpression condExpr2;
		while (condIter.hasNext())
		{
			Condition condition = condIter.next();

			condExpr2 = translateCondition(condition);

			if (condExpr == null)
			{
				condExpr = condExpr2;
			}
			else
			{
				OrCondition or = OrCondition.Factory.newInstance();
				or.setLeft(condExpr);
				or.setRight(condExpr2);
				condExpr = or;
			}
		}

		if (condExpr != null)
		{
			weaveFilterCode.setCheckCondition(condExpr);
		}

		return weaveFilterCode;
	}

	public Object visitBlock(Block block)
	{
		composestar.dotNET2.tym.entities.Block weaveBlock = composestar.dotNET2.tym.entities.Block.Factory
				.newInstance();
		weaveBlock.addNewInstructions();

		setLabel(block, weaveBlock);

		// create contained instructions:
		List<InlineInstruction> inlineInstructions = new ArrayList<InlineInstruction>();
		Iterator<Instruction> instructions = block.getInstructions();
		while (instructions.hasNext())
		{
			Instruction instruction = instructions.next();

			InlineInstruction inlineInstruction = (InlineInstruction) instruction.accept(this);
			inlineInstructions.add(inlineInstruction);
		}

		// add contained instructions to the weaveBlock:
		weaveBlock.getInstructions().setInstructionArray(inlineInstructions.toArray(new InlineInstruction[0]));

		return weaveBlock;
	}

	public Object visitBranch(Branch branch)
	{
		composestar.dotNET2.tym.entities.Branch weaveBranch = composestar.dotNET2.tym.entities.Branch.Factory
				.newInstance();

		setLabel(branch, weaveBranch);

		if (branch.getConditionExpression() != null)
		{
			weaveBranch.setCondition(translateConditionExpression(branch.getConditionExpression()));
		}
		else
		{
			weaveBranch.setCondition(translateCondition(branch.getCondition()));
		}

		weaveBranch.setTrueBlock((composestar.dotNET2.tym.entities.Block) branch.getTrueBlock().accept(this));
		weaveBranch.setFalseBlock((composestar.dotNET2.tym.entities.Block) branch.getFalseBlock().accept(this));

		return weaveBranch;
	}

	public Object visitFilterAction(FilterAction filterAction)
	{
		composestar.dotNET2.tym.entities.FilterAction weaveAction = composestar.dotNET2.tym.entities.FilterAction.Factory
				.newInstance();

		setLabel(filterAction, weaveAction);

		weaveAction.setType(filterAction.getType());

		weaveAction.setFullName(fullNameMap.get(filterAction.getType()));

		weaveAction.setSelector(filterAction.getMessage().getSelector());
		weaveAction.setTarget(filterAction.getMessage().getTarget().getName());

		weaveAction.setSubstitutionSelector(filterAction.getSubstitutedMessage().getSelector());
		weaveAction.setSubstitutionTarget(filterAction.getSubstitutedMessage().getTarget().getName());

		weaveAction.setOnCall(filterAction.isOnCall());
		weaveAction.setReturning(filterAction.isReturning());

		weaveAction.setBookKeeping(filterAction.getBookKeeping());
		weaveAction.setResourceOperations(filterAction.getResourceOperations());

		return weaveAction;
	}

	public Object visitJump(Jump jump)
	{
		JumpInstruction weaveJump = JumpInstruction.Factory.newInstance();

		setLabel(jump, weaveJump);

		weaveJump.setTarget(jump.getTarget().getId());

		return weaveJump;
	}

	private composestar.dotNET2.tym.entities.ConditionExpression translateCondition(Condition condition)
	{
		composestar.dotNET2.tym.entities.ConditionLiteral conditionLiteral = composestar.dotNET2.tym.entities.ConditionLiteral.Factory
				.newInstance();
		conditionLiteral.setName(condition.getName());

		return conditionLiteral;
	}

	private composestar.dotNET2.tym.entities.ConditionExpression translateConditionExpression(
			ConditionExpression expression)
	{
		if (expression instanceof And)
		{
			And and = (And) expression;
			AndCondition weaveAnd = AndCondition.Factory.newInstance();

			weaveAnd.setLeft(translateConditionExpression(and.getLeft()));
			weaveAnd.setRight(translateConditionExpression(and.getRight()));

			return weaveAnd;
		}
		else if (expression instanceof Or)
		{
			Or or = (Or) expression;
			OrCondition weaveOr = OrCondition.Factory.newInstance();

			weaveOr.setLeft(translateConditionExpression(or.getLeft()));
			weaveOr.setRight(translateConditionExpression(or.getRight()));

			return weaveOr;
		}
		else if (expression instanceof Not)
		{
			Not not = (Not) expression;
			NotCondition weaveNot = NotCondition.Factory.newInstance();

			weaveNot.setOperand(translateConditionExpression(not.getOperand()));

			return weaveNot;
		}
		else if (expression instanceof ConditionVariable)
		{
			ConditionVariable literal = (ConditionVariable) expression;
			ConditionLiteral weaveLiteral = ConditionLiteral.Factory.newInstance();

			weaveLiteral.setName(literal.getCondition().getRef().getName());

			return weaveLiteral;
		}
		else if (expression instanceof True)
		{
			return TrueCondition.Factory.newInstance();
		}
		else if (expression instanceof False)
		{
			return FalseCondition.Factory.newInstance();
		}
		else
		{
			throw new RuntimeException("Unknown ConditionExpression");
		}
	}

	private void setLabel(Instruction instruction, InlineInstruction inlineInstruction)
	{
		Label label = instruction.getLabel();

		if (label == null)
		{
			return;
		}

		inlineInstruction.setLabel(label.getId());
	}
}
