/*
 * Created on 24-nov-2006
 *
 */
package Composestar.DotNET2.TYM.RepositoryEmitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterActionInstruction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Visitable;
import Composestar.Core.INLINE.model.Visitor;

public class FilterCodeCompressor
{
	private final static CpsSelector GENERALIZATION_SELECTOR = new CpsSelectorImpl("+");

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
	public int addFilterCode(FilterCode filterCode, CpsSelector startSelector)
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
			Integer newId = Integer.valueOf(generalizedIdMap.size());
			generalizedIdMap.put(eqFilterCode, newId);
			return newId.intValue();
		}
	}

	private FilterCode generalize(FilterCode filterCode, CpsSelector startSelector)
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
		for (Entry<EqualizedFilterCode, Integer> entry : generalizedIdMap.entrySet())
		{
			result[entry.getValue()] = entry.getKey().filterCode;
		}

		return result;
	}

	private static class EqualizedFilterCode
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
			Boolean equals = (Boolean) filterCode.accept(visitor);
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

	private static class EqualsVisitor implements Visitor
	{
		private Visitable currentCheckInstruction;

		public EqualsVisitor(Visitable visitable)
		{
			currentCheckInstruction = visitable;
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

			if (filterCode.getBookKeeping() != filterCode2.getBookKeeping())
			{
				return Boolean.FALSE;
			}

			// Iterate the checkConditions to check whether they are equal
			Iterator<MethodReference> iter1 = filterCode.getCheckConditions();
			Iterator<MethodReference> iter2 = filterCode2.getCheckConditions();
			while (iter1.hasNext())
			{
				if (!iter2.hasNext())
				{
					return Boolean.FALSE;
				}
				else
				{
					MethodReference cond1 = iter1.next();
					MethodReference cond2 = iter2.next();
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
			Iterator<Instruction> blockEnum = block.getInstructions();
			Iterator<Instruction> checkBlockEnum = checkBlock.getInstructions();

			while (blockEnum.hasNext())
			{
				if (!checkBlockEnum.hasNext())
				{
					return Boolean.FALSE;
				}

				currentCheckInstruction = checkBlockEnum.next();
				Instruction nextInstruction = blockEnum.next();
				if (nextInstruction.accept(this).equals(Boolean.FALSE))
				{
					return Boolean.FALSE;
				}
			}

			if (!checkBlockEnum.hasNext())
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
			if (branch.getMatchingExpression() == null)
			{
				if (!branch.getConditionMethod().equals(checkBranch.getConditionMethod()))
				{
					return Boolean.FALSE;
				}
			}
			else if (!branch.getMatchingExpression().equals(checkBranch.getMatchingExpression()))
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
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterActionInstruction)
		 */
		public Object visitFilterAction(FilterActionInstruction filterAction)
		{
			// Check for same class
			if (!(currentCheckInstruction instanceof FilterActionInstruction))
			{
				return Boolean.FALSE;
			}

			FilterActionInstruction checkFilterAction = (FilterActionInstruction) currentCheckInstruction;

			if (checkFilterAction.getBookKeeping() != filterAction.getBookKeeping())
			{
				return Boolean.FALSE;
			}

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

			// TODO messages can be different, but actually be identical
			// messages
			if (!filterAction.getMessage().equals(checkFilterAction.getMessage()))
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

			if (!checkFilterAction.getResourceOperations().equals(filterAction.getResourceOperations()))
			{
				return Boolean.FALSE;
			}

			// TODO not completely correct, different argument instances could
			// have similar values
			if (!filterAction.getArguments().equals(checkFilterAction.getArguments()))
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

	private static class HashCodeVisitor implements Visitor
	{
		public HashCodeVisitor()
		{}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
		 */
		public Object visitFilterCode(FilterCode filterCode)
		{
			int value = 0;
			if (filterCode.getBookKeeping())
			{
				value = 1;
			}
			value += (Integer) filterCode.getInstruction().accept(this);
			return value;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
		 */
		public Object visitBlock(Block block)
		{
			int value = 0;

			// Check whether instructions within block equal
			for (Instruction nextInstruction : block.getInstructionsEx())
			{
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
			if (branch.getMatchingExpression() == null)
			{
				value += branch.getConditionMethod().hashCode();
			}
			else
			{
				value += branch.getMatchingExpression().hashCode();
			}

			// True block
			value += ((Integer) branch.getTrueBlock().accept(this)).intValue();

			// False block
			value += ((Integer) branch.getFalseBlock().accept(this)).intValue();

			return value;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterActionInstruction)
		 */
		public Object visitFilterAction(FilterActionInstruction filterAction)
		{
			int value = 0;

			if (filterAction.getBookKeeping())
			{
				value = 1;
			}
			value += filterAction.getType().hashCode();
			// TODO is stricter than it should be
			value += filterAction.getMessage().hashCode();
			value += filterAction.getResourceOperations().hashCode();
			// TODO is stricter than it should be
			value += filterAction.getArguments().hashCode();

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

	private static class GeneralizeVisitor implements Visitor
	{
		private CpsSelector selector;

		public GeneralizeVisitor(CpsSelector selector)
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
			for (MethodReference condition : filterCode.getCheckConditionsEx())
			{
				copy.addCheckCondition(condition);
			}

			// Instructions
			Instruction instrCopy = (Instruction) filterCode.getInstruction().accept(this);
			copy.setInstruction(instrCopy);
			copy.setBookKeeping(filterCode.getBookKeeping());

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
			for (Instruction nextInstruction : block.getInstructionsEx())
			{
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
			if (branch.getConditionMethod() == null)
			{
				copy = new Branch(branch.getMatchingExpression());
			}
			else
			{
				copy = new Branch(branch.getConditionMethod());
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
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterActionInstruction)
		 */
		public Object visitFilterAction(FilterActionInstruction filterAction)
		{
			CpsMessage message = filterAction.getMessage();
			CpsMessage genMessage = generalize(message);

			// TODO not a deep clone
			Collection<CanonAssignment> args = new ArrayList<CanonAssignment>(filterAction.getArguments());

			FilterActionInstruction copy =
					new FilterActionInstruction(filterAction.getType(), genMessage, args, filterAction.isOnCall(),
							filterAction.isReturning());

			copyLabel(filterAction, copy);
			copy.setBookKeeping(filterAction.getBookKeeping());
			copy.setResourceOperations(filterAction.getResourceOperations());

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

		private CpsMessage generalize(CpsMessage message)
		{
			CpsMessage newMessage;
			try
			{
				newMessage = (CpsMessage) message.clone();
			}
			catch (CloneNotSupportedException e)
			{
				// TODO error
				return message;
			}
			if (newMessage.getSelector().compatible(selector))
			{
				newMessage.setSelector(GENERALIZATION_SELECTOR);
			}
			return newMessage;
		}

	}
}
