/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

/**
 * A jump instruction
 * 
 * @author Arjan
 */
public class Jump extends Instruction
{
	/**
	 * The label to jump to.
	 */
	private Label target;

	/**
	 * The constructor
	 * 
	 * @param toLabel The label to jump to.
	 */
	public Jump(Label toLabel)
	{
		target = toLabel;
	}

	/**
	 * @return the target
	 */
	public Label getTarget()
	{
		return target;
	}

	/**
	 * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
	 */
	public Object accept(Visitor visitor)
	{
		return visitor.visitJump(this);
	}

}
