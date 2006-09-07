package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;
/**
 * Summary description for BreakPointBiLTL.
 */
public abstract class BreakPointMono extends BreakPoint
{
	protected BreakPoint right = null;

	public BreakPointMono(Halter halt, BreakPoint right)
	{
		super(halt);
		setRight(right);
	}

	public boolean isRightNull()
	{
		return right == null;
	}

	public void setRight(BreakPoint right)
	{
		this.right = right;
	}

	public boolean threadSpecific() 
	{
		return right.threadSpecific();
	}
}
