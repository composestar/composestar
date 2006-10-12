package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointAnd extends BreakPointBi
{
	public BreakPointAnd(Halter halt, BreakPoint right, BreakPoint left)
	{
		this(halt,right);
		setLeft(left);
	}

	public BreakPointAnd(Halter halt, BreakPoint right)
	{
		super(halt,right);
	}

	public boolean check(boolean isLeft, boolean isRight)
	{
		return isLeft && isRight;
	}
}
