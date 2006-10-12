package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
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

    public boolean matchEvent(int eventType, Filter currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context){
		stillOn &= right.matchEvent(eventType, currentFilter, beforeMessage, afterMessage, filters, context);
		return stillOn;
	}
}
