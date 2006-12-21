package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointUntil extends BreakPointBi
{
	private boolean first = true;

	public BreakPointUntil(BreakPoint right)
	{
		super(right);
	}

	public boolean check(boolean isLeft,boolean isRight)
	{
		first &= isLeft;
		return (!first) && isRight;
	}
}
