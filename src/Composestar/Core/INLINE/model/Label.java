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

	public Label()
	{
		this.id = -1;
	}

	public Label(int id)
	{
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}
}
