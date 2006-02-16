package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;
import java.util.*;

/**
 * Summary description for BreakPointBiLTL.
 */
public abstract class BreakPointNot extends BreakPointMono
{
	public BreakPointNot(Halter halt, BreakPoint right)
	{
		super(halt, right);
	}

	public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context)
	{
		return !right.matchEvent(eventType, currentFilter, source, message, target, filters, context);;
	}
}
