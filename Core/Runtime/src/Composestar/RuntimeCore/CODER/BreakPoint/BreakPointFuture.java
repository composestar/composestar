package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointFuture extends BreakPointMono
{
	public BreakPointFuture(Halter halt, BreakPoint right)
	{
		super(halt, right);
	}

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context){
		//Just check the last one.
		return right.matchEvent(eventType, currentFilter, beforeMessage, afterMessage, filters, context);
    }
}
