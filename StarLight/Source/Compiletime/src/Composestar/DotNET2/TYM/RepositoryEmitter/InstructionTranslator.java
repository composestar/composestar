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
package Composestar.DotNET2.TYM.RepositoryEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.MECondition;
import Composestar.Core.CpsRepository2.FilterElements.MELiteral;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModuleVariable;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.AndMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.NotMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.OrMEOper;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterActionInstruction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;

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

	public InstructionTranslator(Repository repos)
	{
		for (FilterAction filterAction : repos.getAll(FilterAction.class))
		{
			fullNameMap.put(filterAction.getName(), filterAction.getSystemName());
		}
	}

	public static String getSafeTargetName(CpsObject target)
	{
		if (target.isInnerObject())
		{
			return PropertyNames.INNER;
		}
		if (target.isSelfObject())
		{
			return PropertyNames.SELF;
		}
		if (target instanceof FilterModuleVariable)
		{
			return StarLightEmitterRunner.getUniqueName((FilterModuleVariable) target);
		}
		throw new RuntimeException(String.format("Unnamed target: %s", target));
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
	 */
	public Object visitFilterCode(FilterCode filterCode)
	{
		composestar.dotNET2.tym.entities.FilterCode weaveFilterCode =
				composestar.dotNET2.tym.entities.FilterCode.Factory.newInstance();

		weaveFilterCode.setBookKeeping(filterCode.getBookKeeping());

		// Instructions
		InlineInstruction instruction = (InlineInstruction) filterCode.getInstruction().accept(this);
		weaveFilterCode.setInstructions(instruction);

		// Check conditions
		List<MethodReference> condIter = filterCode.getCheckConditionsEx();
		composestar.dotNET2.tym.entities.ConditionExpression condExpr = null;
		composestar.dotNET2.tym.entities.ConditionExpression condExpr2;
		for (MethodReference condMeth : condIter)
		{
			condExpr2 = translateCondition(condMeth);

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
		composestar.dotNET2.tym.entities.Block weaveBlock =
				composestar.dotNET2.tym.entities.Block.Factory.newInstance();
		weaveBlock.addNewInstructions();

		setLabel(block, weaveBlock);

		// create contained instructions:
		List<InlineInstruction> inlineInstructions = new ArrayList<InlineInstruction>();
		List<Instruction> instructions = block.getInstructionsEx();
		for (Instruction instruction : instructions)
		{
			InlineInstruction inlineInstruction = (InlineInstruction) instruction.accept(this);
			inlineInstructions.add(inlineInstruction);
		}

		// add contained instructions to the weaveBlock:
		weaveBlock.getInstructions().setInstructionArray(inlineInstructions.toArray(new InlineInstruction[0]));

		return weaveBlock;
	}

	public Object visitBranch(Branch branch)
	{
		composestar.dotNET2.tym.entities.Branch weaveBranch =
				composestar.dotNET2.tym.entities.Branch.Factory.newInstance();

		setLabel(branch, weaveBranch);

		if (branch.getMatchingExpression() != null)
		{
			weaveBranch.setCondition(translateConditionExpression(branch.getMatchingExpression()));
		}
		else
		{
			weaveBranch.setCondition(translateCondition(branch.getConditionMethod()));
		}

		weaveBranch.setTrueBlock((composestar.dotNET2.tym.entities.Block) branch.getTrueBlock().accept(this));
		weaveBranch.setFalseBlock((composestar.dotNET2.tym.entities.Block) branch.getFalseBlock().accept(this));

		return weaveBranch;
	}

	public Object visitFilterAction(FilterActionInstruction filterAction)
	{
		composestar.dotNET2.tym.entities.FilterAction weaveAction =
				composestar.dotNET2.tym.entities.FilterAction.Factory.newInstance();

		setLabel(filterAction, weaveAction);

		weaveAction.setType(filterAction.getType());

		weaveAction.setFullName(fullNameMap.get(filterAction.getType()));

		weaveAction.setSelector(filterAction.getMessage().getSelector().getName());
		weaveAction.setTarget(getSafeTargetName(filterAction.getMessage().getTarget()));

		// TODO other arguments

		weaveAction.setOnCall(filterAction.isOnCall());
		weaveAction.setReturning(filterAction.isReturning());

		weaveAction.setBookKeeping(filterAction.getBookKeeping());
		weaveAction.setResourceOperations(filterAction.getResourceOperations());

		// FIXME arguments ....
		// this is a dirty hack
		for (CanonAssignment asgn : filterAction.getArguments())
		{
			if (PropertyNames.TARGET.equalsIgnoreCase(asgn.getProperty().getBaseName()))
			{
				CpsVariable var = asgn.getValue();
				if (var instanceof CpsObject)
				{
					weaveAction.setFilterArgumentTarget(getSafeTargetName((CpsObject) var));
				}
				else
				{
					// TODO: error
				}
			}
			else if (PropertyNames.SELECTOR.equalsIgnoreCase(asgn.getProperty().getBaseName()))
			{
				CpsVariable var = asgn.getValue();
				if (var instanceof CpsSelector)
				{
					weaveAction.setFilterArgumentSelector(((CpsSelector) var).getName());
				}
				else if (var instanceof CpsLiteral)
				{
					weaveAction.setFilterArgumentSelector(((CpsLiteral) var).getLiteralValue());
				}
				else if (var instanceof CpsProgramElement)
				{
					ProgramElement pe = ((CpsProgramElement) var).getProgramElement();
					if (pe instanceof MethodInfo)
					{
						weaveAction.setFilterArgumentSelector(((MethodInfo) pe).getName());
					}
				}
				else
				{
					// TODO: error
				}
			}
		}

		return weaveAction;
	}

	public Object visitJump(Jump jump)
	{
		JumpInstruction weaveJump = JumpInstruction.Factory.newInstance();

		setLabel(jump, weaveJump);

		weaveJump.setTarget(jump.getTarget().getId());

		return weaveJump;
	}

	private composestar.dotNET2.tym.entities.ConditionExpression translateCondition(MethodReference condition)
	{
		composestar.dotNET2.tym.entities.ConditionLiteral conditionLiteral =
				composestar.dotNET2.tym.entities.ConditionLiteral.Factory.newInstance();
		conditionLiteral
				.setName(String.format("%s_%d", condition.getReferenceId(), System.identityHashCode(condition)));

		return conditionLiteral;
	}

	private composestar.dotNET2.tym.entities.ConditionExpression translateConditionExpression(
			MatchingExpression expression)
	{
		if (expression instanceof AndMEOper)
		{
			AndMEOper and = (AndMEOper) expression;
			AndCondition weaveAnd = AndCondition.Factory.newInstance();

			weaveAnd.setLeft(translateConditionExpression(and.getLHS()));
			weaveAnd.setRight(translateConditionExpression(and.getRHS()));

			return weaveAnd;
		}
		else if (expression instanceof OrMEOper)
		{
			OrMEOper or = (OrMEOper) expression;
			OrCondition weaveOr = OrCondition.Factory.newInstance();

			weaveOr.setLeft(translateConditionExpression(or.getLHS()));
			weaveOr.setRight(translateConditionExpression(or.getRHS()));

			return weaveOr;
		}
		else if (expression instanceof NotMEOper)
		{
			NotMEOper not = (NotMEOper) expression;
			NotCondition weaveNot = NotCondition.Factory.newInstance();

			weaveNot.setOperand(translateConditionExpression(not.getOperand()));

			return weaveNot;
		}
		else if (expression instanceof MECondition)
		{
			ConditionLiteral weaveLiteral = ConditionLiteral.Factory.newInstance();
			weaveLiteral.setName(StarLightEmitterRunner.getUniqueName(((MECondition) expression).getCondition()));
			return weaveLiteral;
		}
		else if (expression instanceof MELiteral)
		{
			if (((MELiteral) expression).getLiteralValue())
			{
				return TrueCondition.Factory.newInstance();
			}
			else
			{
				return FalseCondition.Factory.newInstance();
			}
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
