package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointRelease extends BreakPointBi
{
	private boolean armed = false;
	private boolean released = false;

	public BreakPointRelease(Halter halt, BreakPoint right)
	{
		super(halt,right);
	}

	public boolean check(boolean isLeft, boolean isRight)
	{
		armed |= isLeft;
		released |= (armed && !isLeft);
		return released && isRight;
	}
}
