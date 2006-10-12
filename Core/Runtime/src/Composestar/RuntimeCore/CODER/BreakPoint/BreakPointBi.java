package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.FLIRT.Message.*;
import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public abstract class BreakPointBi extends BreakPointMono
{
	protected BreakPoint left = null;

    public BreakPointBi(Halter halt, BreakPoint right)
    {
        super(halt, right);
    }

	public BreakPointBi(Halter halt, BreakPoint left, BreakPoint right)
	{
		super(halt, right);
		setLeft(left);
	}

	public boolean isLeftNull()
	{
		return left == null;
	}

	public void setLeft(BreakPoint left)
	{
		this.left = left;
	}

	public boolean threadSpecific() 
	{
		return left.threadSpecific() || right.threadSpecific();
	}

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context){
		//Calculate everything, things can change
		boolean isLeft = left.matchEvent(eventType, currentFilter, beforeMessage, afterMessage,filters, context);
		boolean isRight = left.matchEvent(eventType, currentFilter, beforeMessage, afterMessage, filters, context);
		return check(isLeft,isRight);
	}

	public abstract boolean check(boolean isLeft,boolean isRight);
}
