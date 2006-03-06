package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;
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

	public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessageList message, Object target, ArrayList filters, Dictionary context)
	{
		//Just check the last one.
		return right.matchEvent(eventType, currentFilter, source, message, target, filters, context);;
	}
}
