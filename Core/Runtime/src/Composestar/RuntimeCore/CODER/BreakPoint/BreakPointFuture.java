package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.ExecutionStackItem;

import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointFuture extends BreakPoint
{
	private BreakPoint right;
	public BreakPointFuture(BreakPoint right)
	{
		this.right= right;
	}

    public boolean check(ExecutionStackItem status){	//Just check the last one.
		return right.check(status);
    }
}
