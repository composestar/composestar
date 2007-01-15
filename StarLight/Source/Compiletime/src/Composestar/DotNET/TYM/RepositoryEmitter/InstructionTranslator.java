package Composestar.DotNET.TYM.RepositoryEmitter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.RepositoryImplementation.DataStore;

import composestar.dotNET.tym.entities.AndCondition;
import composestar.dotNET.tym.entities.ConditionLiteral;
import composestar.dotNET.tym.entities.FalseCondition;
import composestar.dotNET.tym.entities.InlineInstruction;
import composestar.dotNET.tym.entities.JumpInstruction;
import composestar.dotNET.tym.entities.NotCondition;
import composestar.dotNET.tym.entities.OrCondition;
import composestar.dotNET.tym.entities.TrueCondition;

class InstructionTranslator implements Visitor
{
	private Map fullNameMap = new HashMap();

	public InstructionTranslator()
	{
		Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction filterAction;

		DataStore dataStore = DataStore.instance();
		Iterator it = dataStore.getAllInstancesOf(
				Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction.class);
		
		while (it.hasNext())
		{
			filterAction = (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction) it.next();
			fullNameMap.put(filterAction.getName(), filterAction.getFullName());
		}
	}

	public Object visitBlock(Block block)
	{
		composestar.dotNET.tym.entities.Block weaveBlock 
				= composestar.dotNET.tym.entities.Block.Factory.newInstance();
		weaveBlock.addNewInstructions();

		setLabel(block, weaveBlock);

		// create contained instructions:
		Vector inlineInstructions = new Vector();
		Enumeration instructions = block.getInstructions();
		while (instructions.hasMoreElements())
		{
			Instruction instruction = (Instruction) instructions.nextElement();

			InlineInstruction inlineInstruction = (InlineInstruction) instruction.accept(this);
			inlineInstructions.add(inlineInstruction);
		}

		// add contained instructions to the weaveBlock:
		weaveBlock.getInstructions().setInstructionArray(
				(InlineInstruction[]) inlineInstructions.toArray(new InlineInstruction[0]));

		return weaveBlock;
	}

	public Object visitBranch(Branch branch)
	{
		composestar.dotNET.tym.entities.Branch weaveBranch 
				= composestar.dotNET.tym.entities.Branch.Factory.newInstance();

		setLabel(branch, weaveBranch);

		weaveBranch.setCondition(translateConditionExpression(branch.getConditionExpression()));
		weaveBranch.setTrueBlock((composestar.dotNET.tym.entities.Block) branch.getTrueBlock().accept(this));
		weaveBranch.setFalseBlock((composestar.dotNET.tym.entities.Block) branch.getFalseBlock().accept(this));

		return weaveBranch;
	}

	public Object visitFilterAction(FilterAction filterAction)
	{
		composestar.dotNET.tym.entities.FilterAction weaveAction = composestar.dotNET.tym.entities.FilterAction.Factory
				.newInstance();

		setLabel(filterAction, weaveAction);

		weaveAction.setType(filterAction.getType());

		weaveAction.setFullName((String) fullNameMap.get(filterAction.getType()));

		weaveAction.setSelector(filterAction.getMessage().getSelector());
		weaveAction.setTarget(filterAction.getMessage().getTarget().getName());

		weaveAction.setSubstitutionSelector(filterAction.getSubstitutedMessage().getSelector());
		weaveAction.setSubstitutionTarget(filterAction.getSubstitutedMessage().getTarget().getName());
		
		weaveAction.setOnCall(filterAction.isOnCall());
		weaveAction.setReturning(filterAction.isReturning());

		return weaveAction;
	}

	public Object visitJump(Jump jump)
	{
		JumpInstruction weaveJump = JumpInstruction.Factory.newInstance();

		setLabel(jump, weaveJump);

		weaveJump.setTarget(jump.getTarget().getId());

		return weaveJump;
	}

	
	private composestar.dotNET.tym.entities.ConditionExpression translateConditionExpression(
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