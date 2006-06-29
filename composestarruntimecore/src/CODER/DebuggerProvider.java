package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;
import Composestar.RuntimeCore.FLIRT.Reflection.*;

import Composestar.RuntimeCore.CODER.BreakPoint.*;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Debug interface for the ComposeStar runtime.
 */
public abstract class DebuggerProvider extends Debugger{
	BreakPoint breakpoint = new AlwaysBreakBreakPoint();
	public DebuggerProvider()
	{
		instance = this;
	}

	public void start() 
	{
	}

	public void stop() 
	{
	}

	public void reset() 
	{
	}

	public void event(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point)
	{
		if(breakpoint.matchEvent(eventType, currentFilter, messageList, point))
		{
			executeEvent(eventType, currentFilter, messageList, point);
		}
	}

	protected abstract void executeEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point);
}
