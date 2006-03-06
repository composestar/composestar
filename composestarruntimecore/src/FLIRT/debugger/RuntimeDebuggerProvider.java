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
        cleanupInterval = CLEANUP_TUNE_FACTOR ^ 10;
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

    public void addBreakPointListener(BreakPointListener debugger) {
        Assertion.pre(halt.isGlobalHalted(), MODULENAME, "addBreakPointListener needs a halted Runtime");
        if (debugger == null) return;
        if (!breakpoint.threadSpecific()) {
            breakpoint.addBreakPointListener(debugger);
            return;
        }
        Iterator i = statepool.values().iterator();
        while (i.hasNext()) {
            ((StateHandler) i.next()).getBreakPoint().addBreakPointListener(debugger);
        }
    }

    public void removeBreakPointListener(BreakPointListener debugger) {
        Assertion.pre(halt.isGlobalHalted(), MODULENAME, "removeBreakPointListener needs a halted Runtime");
        if (debugger == null) return;
        if (!breakpoint.threadSpecific()) {
            breakpoint.removeBreakPointListener(debugger);
            return;
        }
        Iterator i = statepool.values().iterator();
        while (i.hasNext()) {
            ((StateHandler) i.next()).getBreakPoint().removeBreakPointListener(debugger);
        }
    }

    public void fireEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessageList message, Object target, ArrayList filters, Dictionary context) {
        Debug.out(Debug.MODE_DEBUG, MODULENAME, "Having event");
        StateHandler handler = getStateHandler();
        handler.event(eventType, currentFilter, source, message, target, filters, context);
    }

    private HashMap statepool = new HashMap();

    //Automatic tuning of the cleaner
    private static int cleanupInterval = 1;
	private static int cleanupTimer = 0;

    private final static int CLEANUP_TUNE_FACTOR = 2;

    private void tuneCleanupInterval(int deaths) {
        switch (deaths) {
            case 0:
                cleanupInterval *= CLEANUP_TUNE_FACTOR; //nothing to do, speed down
                break;
            case 1:
            case CLEANUP_TUNE_FACTOR:
                //On target
                break;
            default:
                cleanupInterval /= CLEANUP_TUNE_FACTOR; //Dead threads, speed up
                break;
        }
		if(cleanupInterval < 0)
		{
			cleanupInterval = Integer.MAX_VALUE;
		}
    }

    private synchronized void cleanup() {
		if(cleanupTimer < cleanupInterval)
		{
			cleanupTimer++;
			return;
		}
		cleanupTimer = 0;

        Debug.out(Debug.MODE_DEBUG, "MODULENAME", "Cleaning RuntimeState");
        int deaths = 0;
        Iterator i = statepool.keySet().iterator();
        while (i.hasNext()) {
            Thread thread = (Thread) i.next();
            if (!thread.isAlive()) {
                statepool.remove(thread);
                deaths++;
            }
        }
        tuneCleanupInterval(deaths);
    }

    private synchronized StateHandler getStateHandler() {
        Thread thread = Thread.currentThread();
        StateHandler handler = (StateHandler) statepool.get(thread);
		Halter halter = new RuntimeHalter();
        if (handler == null) {
            handler = new StateHandler(thread, breakpoint,halter);
            statepool.put(thread, handler);
        }

		cleanup();
        return handler;
    }

    //Breakpoints
    private Halter halt = new RuntimeHalter();
    private BreakPoint dummy = new NeverBreakBreakPoint(halt);
    private BreakPoint breakpoint;

    private void setBreakPoint(BreakPoint breakpoint) {
        if (breakpoint == null) breakpoint = dummy;
        Iterator i = statepool.values().iterator();
        while (i.hasNext()) {
            ((StateHandler) i.next()).setBreakPoint(breakpoint);
        }
    }
}
