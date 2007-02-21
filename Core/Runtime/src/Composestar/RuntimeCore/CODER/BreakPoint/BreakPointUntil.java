package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointUntil extends BreakPoint
{
	private boolean first = true;
	private BreakPoint left;
	private BreakPoint right;

	public BreakPointUntil(BreakPoint left, BreakPoint right)
	{
		this.left = left;
		this.right = right;
	}

	public boolean check(ExecutionStackItem status){
		first &= left.check(status);
		return (!first) && right.check(status);
	}
}
