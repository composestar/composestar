package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
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

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point){
		//Calculate everything, things can change
		boolean isLeft = left.matchEvent(eventType, currentFilter, messageList,point);
		boolean isRight = left.matchEvent(eventType, currentFilter, messageList, point);
		return check(isLeft,isRight);
	}

	public abstract boolean check(boolean isLeft,boolean isRight);
}
