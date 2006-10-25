/*
 * Created on 27-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * A case instruction, a subinstruction of a switch.
 * 
 * @author Arjan
 */
public class Case extends Instruction
{
	/**
	 * The value to check the switchexpression against. When it has this value,
	 * this case should be executed.
	 */
	private int checkConstant;

	/**
	 * An instructionblock containing the instructions in this case.
	 */
	private Block instructions;

	/**
	 * The constructor.
	 * 
	 * @param checkConstant The value to check the switchexpression against.
	 * @param instructions An instructionblock containing the instructions in
	 *            this case.
	 */
	public Case(int checkConstant, Block instructions)
	{
		this.checkConstant = checkConstant;
		this.instructions = instructions;
	}

	/**
	 * @return the checkConstant
	 */
	public int getCheckConstant()
	{
		return checkConstant;
	}

	/**
	 * @return the instructions
	 */
	public Block getInstructions()
	{
		return instructions;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitCase(this);
	}

}
