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
	{

	}

	/**
	 * Creates an instruction with a label.
	 * 
	 * @param label The label of the instruction.
	 */
	public Instruction(Label label)
	{
		this.label = label;
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
	 * @param label the label to set
	 */
	public void setLabel(Label label)
	{
		this.label = label;
	}
}
