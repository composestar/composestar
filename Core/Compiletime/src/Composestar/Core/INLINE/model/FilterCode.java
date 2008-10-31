package Composestar.Core.INLINE.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsRepository2.References.MethodReference;

/**
 * Contains the filter code for one method/call
 * 
 * @author Arjan
 */
public class FilterCode implements Visitable
{
	/**
	 * The conditions to check before filter code execution.
	 */
	private List<MethodReference> checkConditions;

	/**
	 * The filter code instructions
	 */
	private Instruction instruction;

	/**
	 * If true perform resource operation bookkeeping for this code block.
	 */
	private boolean bookkeeping;

	/**
	 * The constructor
	 */
	public FilterCode()
	{
		checkConditions = new ArrayList<MethodReference>();
	}

	/**
	 * Adds a condition that needs to be checked before filter code can be
	 * executed.
	 * 
	 * @param condition
	 */
	public void addCheckCondition(MethodReference condition)
	{
		checkConditions.add(condition);
	}

	/**
	 * Returns the conditions to check before filter code can be executed.
	 * 
	 * @return
	 * @deprecated use getCheckConditionsEx
	 */
	@Deprecated
	public Iterator<MethodReference> getCheckConditions()
	{
		return getCheckConditionsEx().iterator();
	}

	public List<MethodReference> getCheckConditionsEx()
	{
		return Collections.unmodifiableList(checkConditions);
	}

	/**
	 * @return the instruction
	 */
	public Instruction getInstruction()
	{
		return instruction;
	}

	/**
	 * @param instruction the instruction to set
	 */
	public void setInstruction(Instruction instruction)
	{
		this.instruction = instruction;
	}

	/**
	 * @param value
	 * @see #bookkeeping
	 */
	public void setBookKeeping(boolean value)
	{
		bookkeeping = value;
	}

	/**
	 * @return
	 * @see #bookkeeping
	 */
	public boolean getBookKeeping()
	{
		return bookkeeping;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitFilterCode(this);
	}
}
