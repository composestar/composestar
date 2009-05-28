/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * A label that can be attached to an instruction, so that there can be a jump
 * to that instruction
 * 
 * @author Arjan
 */
public class Label
{
	/**
	 * The id of the label
	 */
	private int id;

	/**
	 * Flag set to true when the label is used as a target for a jump
	 */
	private boolean used;

	public Label()
	{
		id = -1;
	}

	public Label(int labelId)
	{
		id = labelId;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param value the id to set
	 */
	public void setId(int value)
	{
		id = value;
	}

	/**
	 * @return true when the label is used as a jump target
	 */
	public boolean isUsed()
	{
		return used;
	}

	/**
	 * Set the usage of this label as a jump target
	 * 
	 * @param value
	 */
	public void setUsed(boolean value)
	{
		used = value;
	}
}
