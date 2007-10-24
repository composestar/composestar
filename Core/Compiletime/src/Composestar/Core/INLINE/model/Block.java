/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A Block instruction is a container for other instructions
 * 
 * @author Arjan
 */
public class Block extends Instruction
{
	/**
	 * The instructions contained in this block.
	 */
	private List<Instruction> instructions;

	/**
	 * The constructor
	 */
	public Block()
	{
		instructions = new ArrayList<Instruction>();
	}

	/**
	 * Adds an instruction to the end of this block.
	 * 
	 * @param instruction The instruction to add.
	 */
	public void addInstruction(Instruction instruction)
	{
		instructions.add(instruction);
	}

	/**
	 * @return An enumeration of the instructions in this block.
	 */
	public Iterator<Instruction> getInstructions()
	{
		return instructions.iterator();
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitBlock(this);
	}

}
