package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;

import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public abstract class BreakPointNot extends BreakPointMono
{
	public BreakPointNot(BreakPoint right)
	{
		super(right);
	}

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point){
			return !right.matchEvent(eventType, currentFilter, messageList, point);
    }
}
