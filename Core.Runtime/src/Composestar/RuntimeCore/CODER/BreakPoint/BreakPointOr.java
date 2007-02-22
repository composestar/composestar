package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointOr extends BreakPointBi
{
	public BreakPointOr(BreakPoint right, BreakPoint left)
	{
		this(right);
		setLeft(left);
	}

	public BreakPointOr(BreakPoint right)
	{
		super(right);
	}

	public boolean check(boolean isLeft, boolean isRight)
	{
		return isLeft || isRight;
	}
}
