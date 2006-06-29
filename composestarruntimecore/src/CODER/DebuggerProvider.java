package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;

import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Debug interface for the ComposeStar runtime.
 */
public abstract class DebuggerProvider {
    protected static DebuggerProvider instance = null;

    public DebuggerProvider() {
        instance = this;
    }

    public abstract Halter getHalter();

    public abstract void HaltRuntime();

    public abstract void ResumeRuntime();

    public abstract void addBreakPoint(BreakPoint point);

    public abstract void removeBreakPoint(BreakPoint point);

    public abstract void clearBreakPoints();

    public abstract void addBreakPointListener(BreakPointListener debugger);

    public abstract void removeBreakPointListener(BreakPointListener debugger);


    public static void event(int eventType, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context) {
        instance.fireEvent(eventType, currentFilter, beforeMessage, afterMessage, filters, context);
    }

    public abstract void fireEvent(int eventType, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context);
}
