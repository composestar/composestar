/*
 * Created on 24-nov-2006
 *
 */
package Composestar.DotNET.TYM.RepositoryEmitter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Visitable;
import Composestar.Core.INLINE.model.Visitor;

public class FilterCodeCompressor
{
	private final static String GENERALIZATION_SELECTOR = "+";
	
	private HashMap<EqualizedFilterCode, Integer> generalizedIdMap;

	public FilterCodeCompressor()
	{
		generalizedIdMap = new HashMap<EqualizedFilterCode, Integer>();
	}

	/**
	 * Adds the given FilterCode to the compressor and returns the id of the
	 * corresponding generalized FilterCode. The given FilterCode is generalized
	 * using the startSelector
	 * 
	 * @param filterCode
	 * @param startSelector
	 * @return
	 */
	public int addFilterCode(FilterCode filterCode, String startSelector)
	{
		// Create the generalization
		FilterCode generalizedFilterCode = generalize(filterCode, startSelector);
		EqualizedFilterCode eqFilterCode = new EqualizedFilterCode(generalizedFilterCode);

		// Check whether the generalization already exists
		if (generalizedIdMap.containsKey(eqFilterCode))
		{
			// If it exists, return the id
			Integer id = generalizedIdMap.get(eqFilterCode);
			return id.intValue();
		}
		else
		{
			// If it does not exist, generate id and add it to the map:
			Integer newId = new Integer(generalizedIdMap.size());
			generalizedIdMap.put(eqFilterCode, newId);
			return newId.intValue();
		}
	}

	private FilterCode generalize(FilterCode filterCode, String startSelector)
	{
		return (FilterCode) filterCode.accept(new GeneralizeVisitor(startSelector));
	}

	/**
	 * Returns the list of generalized filtercode objects.
	 * 
	 * @return
	 */
	public FilterCode[] getGeneralizedFilterCodes()
	{
		FilterCode[] result = new FilterCode[generalizedIdMap.size()];

		Iterator<EqualizedFilterCode> filterCodeIter = generalizedIdMap.keySet().iterator();
		while (filterCodeIter.hasNext())
		{
			EqualizedFilterCode equalizedFilterCode = filterCodeIter.next();
			Integer id = generalizedIdMap.get(equalizedFilterCode);
			result[id.intValue()] = equalizedFilterCode.filterCode;
		}

		return result;
	}

	private class EqualizedFilterCode
	{
		private FilterCode filterCode;

		public EqualizedFilterCode(FilterCode filterCode)
		{
			this.filterCode = filterCode;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof EqualizedFilterCode))
			{
				return false;
			}

			EqualizedFilterCode eqFilterCode = (EqualizedFilterCode) obj;
			EqualsVisitor visitor = new EqualsVisitor(eqFilterCode.filterCode);
			Boolean equals = (Boolean) this.filterCode.accept(visitor);
			return equals.booleanValue();
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			HashCodeVisitor visitor = new HashCodeVisitor();
			Integer hashCode = (Integer) filterCode.accept(visitor);
			return hashCode.intValue();
		}

	}

	private class EqualsVisitor implements Visitor
	{
		private Visitable currentCheckInstruction;

		public EqualsVisitor(Visitable visitable)
		{
			this.currentCheckInstruction = visitable;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
		 */
		public Object visitFilterCode(FilterCode filterCode)
		{
			if (!(currentCheckInstruction instanceof FilterCode))
			{
				return Boolean.FALSE;
			}

			FilterCode filterCode2 = (FilterCode) currentCheckInstruction;

			// Iterate the checkConditions to check whether they are equal
			Iterator iter1 = filterCode.getCheckConditions();
			Iterator iter2 = filterCode2.getCheckConditions();
			while (iter1.hasNext())
			{
				if (!iter2.hasNext())
				{
					return Boolean.FALSE;
				}
				else
				{
					Condition cond1 = (Condition) iter1.next();
					Condition cond2 = (Condition) iter2.next();
					if (cond1 != cond2)
					{
						return Boolean.FALSE;
					}
				}
			}
			if (iter2.hasNext())
			{
				return Boolean.FALSE;
			}

			// Check the instructions
			currentCheckInstruction = filterCode2.getInstruction();

			return filterCode.getInstruction().accept(this);
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
		 */
		public Object visitBlock(Block block)
		{
			// Check for same class
			if (!(currentCheckInstruction instanceof Block))
			{
				return Boolean.FALSE;
			}

			Block checkBlock = (Block) currentCheckInstruction;

			// Check whether label equals
			if (!checkLabel(block, checkBlock))
			{
				return Boolean.FALSE;
			}

			// Check whether instructions within block equal
			Enumeration blockEnum = block.getInstructions();
			Enumeration checkBlockEnum = checkBlock.getInstructions();

			while (blockEnum.hasMoreElements())
			{
				if (!checkBlockEnum.hasMoreElements())
				{
					return Boolean.FALSE;
				}

				currentCheckInstruction = (Instruction) checkBlockEnum.nextElement();
				Instruction nextInstruction = (Instruction) blockEnum.nextElement();
				if (nextInstruction.accept(this).equals(Boolean.FALSE))
				{
					return Boolean.FALSE;
				}
			}

			if (!checkBlockEnum.hasMoreElements())
			{
				return Boolean.TRUE;
			}
			else
			{
				return Boolean.FALSE;
			}
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
		 */
		public Object visitBranch(Branch branch)
		{
			// Check for same class
			if (!(currentCheckInstruction instanceof Branch))
			{
				return Boolean.FALSE;
			}

			Branch checkBranch = (Branch) currentCheckInstruction;

			// Check whether label equals
			if (!checkLabel(branch, checkBranch))
			{
				return Boolean.FALSE;
			}

			// Check whether condition expression equals
			if (branch.getConditionExpression() == null)
			{
				if (!branch.getCondition().equals(checkBranch.getCondition()))
				{
					return Boolean.FALSE;
				}
			}
			else if (!branch.getConditionExpression().equals(checkBranch.getConditionExpression()))
			{
				return Boolean.FALSE;
			}

			// Check whether true branch equals
			currentCheckInstruction = checkBranch.getTrueBlock();
			if (branch.getTrueBlock().accept(this).equals(Boolean.FALSE))
			{
				return Boolean.FALSE;
			}

			// Check whether false branch equals
			currentCheckInstruction = checkBranch.getFalseBlock();
			return branch.getFalseBlock().accept(this);
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
		 */
		public Object visitFilterAction(FilterAction filterAction)
		{
			// Check for same class
			if (!(currentCheckInstruction instanceof FilterAction))
			{
				return Boolean.FALSE;
			}

			FilterAction checkFilterAction = (FilterAction) currentCheckInstruction;

			// Check whether label equals
			if (!checkLabel(filterAction, checkFilterAction))
			{
				return Boolean.FALSE;
			}

			// Check whether properties equal
			if (!filterAction.getType().equals(checkFilterAction.getType()))
			{
				return Boolean.FALSE;
			}

			if (!filterAction.getMessage().equals(checkFilterAction.getMessage()))
			{
				return Boolean.FALSE;
			}

			if (!filterAction.getSubstitutedMessage().equals(checkFilterAction.getSubstitutedMessage()))
			{
				return Boolean.FALSE;
			}

			if (filterAction.isOnCall() != checkFilterAction.isOnCall())
			{
				return Boolean.FALSE;
			}

			if (filterAction.isReturning() != checkFilterAction.isReturning())
			{
				return Boolean.FALSE;
			}

			return Boolean.TRUE;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
		 */
		public Object visitJump(Jump jump)
		{
			// Check for same class
			if (!(currentCheckInstruction instanceof Jump))
			{
				return Boolean.FALSE;
			}

			Jump checkJump = (Jump) currentCheckInstruction;

			// Check whether label equals
			if (!checkLabel(jump, checkJump))
			{
				return Boolean.FALSE;
			}

			if (jump.getTarget().getId() == checkJump.getTarget().getId())
			{
				return Boolean.TRUE;
			}
			else
			{
				return Boolean.FALSE;
			}
		}

		private boolean checkLabel(Instruction instruction1, Instruction instruction2)
		{
			if (instruction1.getLabel() == null)
			{
				return instruction2.getLabel() == null;
			}
			else
			{
				return instruction1.getLabel().getId() == instruction2.getLabel().getId();
			}
		}

	}

	private class HashCodeVisitor implements Visitor
	{
		public HashCodeVisitor()
		{}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
		 */
		public Object visitFilterCode(FilterCode filterCode)
		{
			return filterCode.getInstruction().accept(this);
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
		 */
		public Object visitBlock(Block block)
		{
			int value = 0;

			// Check whether instructions within block equal
			Enumeration blockEnum = block.getInstructions();

			while (blockEnum.hasMoreElements())
			{
				Instruction nextInstruction = (Instruction) blockEnum.nextElement();
				value += ((Integer) nextInstruction.accept(this)).intValue();
			}

			return value;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
		 */
		public Object visitBranch(Branch branch)
		{
			int value = 0;

			// condition expression
			if (branch.getConditionExpression() == null)
			{
				value += branch.getCondition().hashCode();
			}
			else
			{
				value += branch.getConditionExpression().hashCode();
			}

			// True block
			value += ((Integer) branch.getTrueBlock().accept(this)).intValue();

			// False block
			value += ((Integer) branch.getFalseBlock().accept(this)).intValue();

			return value;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
		 */
		public Object visitFilterAction(FilterAction filterAction)
		{
			int value = 0;

			value += filterAction.getType().hashCode();
			value += filterAction.getMessage().hashCode();
			value += filterAction.getSubstitutedMessage().hashCode();

			return value;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
		 */
		public Object visitJump(Jump jump)
		{
			int value = jump.getTarget().getId();

			return value;
		}
	}

	private class GeneralizeVisitor implements Visitor
	{
		private String selector;

		public GeneralizeVisitor(String selector)
		{
			this.selector = selector;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
		 */
		public Object visitFilterCode(FilterCode filterCode)
		{
			FilterCode copy = new FilterCode();

			// Check conditions
			Iterator condIter = filterCode.getCheckConditions();
			while (condIter.hasNext())
			{
				Condition condition = (Condition) condIter.next();
				copy.addCheckCondition(condition);
			}

			// Instructions
			Instruction instrCopy = (Instruction) filterCode.getInstruction().accept(this);
			copy.setInstruction(instrCopy);

			return copy;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
		 */
		public Object visitBlock(Block block)
		{
			Block copy = new Block();

			// Label
			copyLabel(block, copy);

			// Instructions in block
			Enumeration blockEnum = block.getInstructions();

			while (blockEnum.hasMoreElements())
			{
				Instruction nextInstruction = (Instruction) blockEnum.nextElement();
				Instruction copyInstruction = (Instruction) nextInstruction.accept(this);
				copy.addInstruction(copyInstruction);
			}

			return copy;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
		 */
		public Object visitBranch(Branch branch)
		{
			Branch copy;

			// condition
			if (branch.getCondition() == null)
			{
				copy = new Branch(branch.getConditionExpression());
			}
			else
			{
				copy = new Branch(branch.getCondition());
			}

			// label
			copyLabel(branch, copy);

			// True block
			copy.setTrueBlock((Block) branch.getTrueBlock().accept(this));

			// False block
			copy.setFalseBlock((Block) branch.getFalseBlock().accept(this));

			return copy;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
		 */
		public Object visitFilterAction(FilterAction filterAction)
		{
			Message message = filterAction.getMessage();
			Message genMessage = generalize(message);

			Message substMessage = filterAction.getSubstitutedMessage();
			Message genSubstMessage = generalize(substMessage);

			FilterAction copy = new FilterAction(filterAction.getType(), genMessage, genSubstMessage, filterAction
					.isOnCall(), filterAction.isReturning());

			copyLabel(filterAction, copy);

			return copy;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
		 */
		public Object visitJump(Jump jump)
		{
			Label copyTarget = new Label(jump.getTarget().getId());

			Jump copy = new Jump(copyTarget);

			copyLabel(jump, copy);

			return copy;
		}

		private void copyLabel(Instruction source, Instruction target)
		{
			if (source.getLabel() != null)
			{
				target.setLabel(new Label(source.getLabel().getId()));
			}
			else
			{
				target.setLabel(null);
			}
		}

		private Message generalize(Message message)
		{
			return new Message(message.getTarget(),
					(message.getSelector().equals(selector)) ? GENERALIZATION_SELECTOR : message.getSelector());

		}

	}
}