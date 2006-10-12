package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
/**
 * Summary description for BreakPointBiLTL.
 */
public abstract class BreakPointMono implements BreakPoint
{
	protected BreakPoint right = null;

	public BreakPointMono(BreakPoint right)
	{
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
}
