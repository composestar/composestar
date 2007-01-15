package Composestar.Core.INLINE.model;

import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;

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
	private ArrayList checkConditions;

	/**
	 * The filter code instructions
	 */
	private Instruction instruction;

	/**
	 * The constructor
	 */
	public FilterCode()
	{
		checkConditions = new ArrayList();
	}

	/**
	 * Adds a condition that needs to be checked before filter code can be
	 * executed.
	 * 
	 * @param condition
	 */
	public void addCheckCondition(Condition condition)
	{
		checkConditions.add(condition);
	}

	/**
	 * Returns the conditions to check before filter code can be executed.
	 * 
	 * @return
	 */
	public Iterator getCheckConditions()
	{
		return checkConditions.iterator();
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
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitFilterCode(this);
	}
}
