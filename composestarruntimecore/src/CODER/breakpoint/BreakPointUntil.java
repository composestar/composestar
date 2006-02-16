package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointUntil extends BreakPointBi
{
	private boolean first = true;

	public BreakPointUntil(Halter halt, BreakPoint right)
	{
		super(halt,right);
	}

	public boolean check(boolean isLeft,boolean isRight)
	{
		first &= isLeft;
		return (!first) && isRight;
	}
}
