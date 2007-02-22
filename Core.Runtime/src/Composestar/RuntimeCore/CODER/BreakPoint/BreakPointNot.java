package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public abstract class BreakPointNot extends BreakPoint
{
	private BreakPoint right;

	public BreakPointNot(BreakPoint right)
	{
		this.right = right;
	}

    public boolean check(ExecutionStackItem status){
		return !right.check(status);
    }
}
