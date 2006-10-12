package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;
import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointGlobal extends BreakPointMono
{
	protected boolean stillOn = true;

	public BreakPointGlobal(Halter halt, BreakPoint right)
	{
		super(halt, right);
	}

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context){
		stillOn &= right.matchEvent(eventType, currentFilter, beforeMessage, afterMessage, filters, context);
		return stillOn;
	}
}