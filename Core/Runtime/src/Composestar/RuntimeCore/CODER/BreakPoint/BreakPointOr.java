package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointOr extends BreakPoint
{
	private BreakPoint left;
	private BreakPoint right;

	public BreakPointOr(BreakPoint right, BreakPoint left)
	{
		this.left = left;
		this.right = right;
	}

	public boolean check(ExecutionStackItem status)
	{
		return right.check(status) || left.check(status);
	}
}