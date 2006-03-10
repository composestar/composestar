package Composestar.RuntimeCore.FLIRT.Debugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.Utils.*;
import Composestar.RuntimeCore.CODER.StateHandler;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Debug interface for the ComposeStar runtime.
 */
public class RuntimeDebuggerProvider extends DebuggerProvider {
    private final static String MODULENAME = "FLIRT(RuntimeDebuggerProvider)";

    public RuntimeDebuggerProvider() {
        breakpoint = dummy;
    }

    public void HaltRuntime() {
        halt.globalSuspend();
    }

    public void ResumeRuntime() {
        halt.globalResume();
    }

    public Halter getHalter() {
        return halt;
    }

    public void addBreakPoint(BreakPoint point) {
        Assertion.pre(halt.isGlobalHalted(), MODULENAME, "addBreakpoint needs a halted Runtime");
        if (point == null) return;
        setBreakPoint(breakpoint.add(point));
        breakpoint = point;
    }

    public void removeBreakPoint(BreakPoint point) {
        Assertion.pre(halt.isGlobalHalted(), MODULENAME, "removeBreakpoint needs a halted Runtime");
        if (point == null) return;
        setBreakPoint(breakpoint.remove(point));
        breakpoint = point;
    }

    public synchronized void clearBreakPoints() {
        breakpoint = dummy;
        setBreakPoint(dummy);
    }

    public synchronized void addBreakPointListener(BreakPointListener debugger) {
        Assertion.pre(halt.isGlobalHalted(), MODULENAME, "addBreakPointListener needs a halted Runtime");
        if (debugger == null) return;
        if (!breakpoint.threadSpecific()) {
            breakpoint.addBreakPointListener(debugger);
            return;
        }

		for(int i = 0 ; i < runningHandlers.size(); i++)
		{
			((StateHandler) runningHandlers.get(i)).getBreakPoint().addBreakPointListener(debugger);
		}
		for(int i = 0 ; i < sleepingHandlers.size(); i++)
		{
			((StateHandler) sleepingHandlers.get(i)).getBreakPoint().addBreakPointListener(debugger);
		}
    }

    public synchronized void removeBreakPointListener(BreakPointListener debugger) {
        Assertion.pre(halt.isGlobalHalted(), MODULENAME, "removeBreakPointListener needs a halted Runtime");
        if (debugger == null) return;
        if (!breakpoint.threadSpecific()) {
            breakpoint.removeBreakPointListener(debugger);
            return;
        }

		for(int i = 0 ; i < runningHandlers.size(); i++)
		{
			((StateHandler) runningHandlers.get(i)).getBreakPoint().removeBreakPointListener(debugger);
		}
		for(int i = 0 ; i < sleepingHandlers.size(); i++)
		{
			((StateHandler) sleepingHandlers.get(i)).getBreakPoint().removeBreakPointListener(debugger);
		}
    }

    public void fireEvent(int eventType, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context) {
        Debug.out(Debug.MODE_DEBUG, MODULENAME, "Having event");
        StateHandler handler = getStateHandler();
        handler.event(eventType, currentFilter, beforeMessage, afterMessage, filters, context);
		returnHandler(handler);
    }

    private ArrayList runningHandlers = new ArrayList();
	private ArrayList sleepingHandlers = new ArrayList();

	private StateHandler createHandler()
	{
		Halter halter = new RuntimeHalter();
		return new StateHandler(breakpoint,halter);
	}

	private synchronized void returnHandler(StateHandler handler)
	{	
		int index = runningHandlers.indexOf(handler);
		runningHandlers.remove(index);
		sleepingHandlers.add(handler);
	}

    private synchronized StateHandler getStateHandler() {
		StateHandler handler = null;
		if(sleepingHandlers.isEmpty())
		{
			handler = createHandler();
			runningHandlers.add(handler);
		}
		else
		{
			handler = (StateHandler) sleepingHandlers.get(sleepingHandlers.size() -1);
			sleepingHandlers.remove(sleepingHandlers.size() -1);
			runningHandlers.add(handler);
			handler.cleanup();
		}
        return handler;
    }

    //Breakpoints
    private Halter halt = new RuntimeHalter();
    private BreakPoint dummy = new NeverBreakBreakPoint(halt);
    private BreakPoint breakpoint;

    private synchronized void setBreakPoint(BreakPoint breakpoint) {
        if (breakpoint == null) breakpoint = dummy;

		for(int i = 0 ; i < runningHandlers.size(); i++)
		{
			((StateHandler) runningHandlers.get(i)).setBreakPoint(breakpoint.getForNextThread());
		}
		for(int i = 0 ; i < sleepingHandlers.size(); i++)
		{
			((StateHandler) sleepingHandlers.get(i)).setBreakPoint(breakpoint.getForNextThread());
		}
    }
}
