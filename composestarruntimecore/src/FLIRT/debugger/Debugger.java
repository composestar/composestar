package Composestar.RuntimeCore.FLIRT.Debugger;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
/**
 * Summary description for Debugger.
 */
public abstract class Debugger {
	public static final int FILTER_ACCEPTED = 0;
	public static final int FILTER_REJECTED = 1;
	public static final int FILTER_EVALUATION_START = 2;
	public static final int MESSAGE_INTERCEPTED = 3;
	public static final int MESSAGE_PROCESSING_START = 4;

	protected static Debugger instance = new Ignore();
	public static Debugger getInstance()
	{
		return instance;
	}

    public abstract void start();

    public abstract void stop();

    public abstract void reset();

	public abstract void event(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point);
}
