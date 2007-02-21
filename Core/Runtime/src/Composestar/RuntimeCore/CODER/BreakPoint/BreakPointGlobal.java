package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointGlobal extends BreakPoint
{
	private boolean stillOn = true;
	private BreakPoint right;

	public BreakPointGlobal(BreakPoint right)
	{
		this.right = right;
	}

    public boolean check(ExecutionStackItem status){
		stillOn &= right.check(status);
		return stillOn;
	}
}
