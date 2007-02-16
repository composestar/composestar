/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import java.util.Enumeration;
import java.util.Vector;

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
	private Vector instructions;

	/**
	 * The constructor
	 */
	public Block()
	{
		instructions = new Vector();
	}

	/**
	 * Adds an instruction to the end of this block.
	 * 
	 * @param instruction The instruction to add.
	 */
	public void addInstruction(Instruction instruction)
	{
		instructions.addElement(instruction);
	}

	/**
	 * @return An enumeration of the instructions in this block.
	 */
	public Enumeration getInstructions()
	{
		return instructions.elements();
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitBlock(this);
	}

}
