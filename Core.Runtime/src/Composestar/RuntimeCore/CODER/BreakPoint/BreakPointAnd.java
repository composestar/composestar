package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointAnd extends BreakPointBi
{
	public BreakPointAnd(BreakPoint right, BreakPoint left)
	{
		this(right);
		setLeft(left);
	}

	public BreakPointAnd(BreakPoint right)
	{
		super(right);
	}

	public boolean check(boolean isLeft, boolean isRight)
	{
		return isLeft && isRight;
	}
}
