/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * Abstract superclass of all instructions.
 * 
 * @author Arjan
 */
public abstract class Instruction implements Visitable
{
	/**
	 * The label of the instruction, so that a jump statement can jump to the
	 * instruction.
	 */
	private Label label;

	/**
	 * Creates an instruction without a label.
	 */
	public Instruction()
	{}

	/**
	 * Creates an instruction with a label.
	 * 
	 * @param instructionLabel The label of the instruction.
	 */
	public Instruction(Label instructionLabel)
	{
		label = instructionLabel;
	}

	/**
	 * @return the label
	 */
	public Label getLabel()
	{
		return label;
	}

	/**
	 * To change the label.
	 * 
	 * @param value the label to set
	 */
	public void setLabel(Label value)
	{
		label = value;
	}
}
