package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointRelease extends BreakPoint
{
	private boolean armed = false;
	private boolean released = false;
	private BreakPoint left;
	private BreakPoint right;

	public BreakPointRelease(BreakPoint left, BreakPoint right)
	{
		this.left = left;
		this.right = right;
	}

	public boolean check(ExecutionStackItem status)
	{
		boolean isLeft = left.check(status);
		armed |= isLeft;
		released |= (armed && !isLeft);
		return released && right.check(status);
	}
}
