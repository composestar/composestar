package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessageList;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for BreakPoint.
 */
public abstract class BreakPoint {
    protected Halter halt = null;

    public BreakPoint(Halter halt) {
        this.halt = halt; //Next stop, Project HALT
    }

    public abstract boolean matchEvent(int eventType, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context);

    public void testEvent(StateHandler handler, int eventType, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage,DebuggableMessageList afterMessage, ArrayList filters, Dictionary context){
        if (matchEvent(eventType, currentFilter, beforeMessage, afterMessage, filters, context)) {
            event(eventType, handler, currentFilter, beforeMessage, afterMessage, filters, context);
        }
    }

    public BreakPoint getForNextThread() {
        return threadSpecific() ? getCopy() : this;
    }

    //Allows us to not use clone -> speed
    protected BreakPoint getCopy() {
        BreakPoint point = null;
        try {
            point = (BreakPoint) clone();
        } catch (Exception e) {
        }
        return point == null ? this : point;
    }

    // Override when breakpoint uses information from multiple threads.
    // e.g. PI Calculus instead of LTL or Regular expressions
    public boolean threadSpecific() {
        return true;
    }

    protected static BreakPointListener[] listeners = new BreakPointListener[0];

    public void event(int eventType, StateHandler handler ,DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context) {
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].breakEvent(eventType, handler,currentFilter, beforeMessage, afterMessage, filters, context);
        }
    }

    public BreakPoint add(BreakPoint point) {
        MultiBreakPoint mpoint = new MultiBreakPoint(halt);
        mpoint.addBreakpoint(point);
        return mpoint;
    }

    public BreakPoint remove(BreakPoint point) {
        if (point.equals(this)) {
            return new NeverBreakBreakPoint(halt);
        } else {
            return this;
        }
    }

    public void addBreakPointListener(BreakPointListener debugger) {
        BreakPointListener[] temp = new BreakPointListener[listeners.length + 1];
        System.arraycopy(listeners, 0, temp, 0, listeners.length);
        temp[listeners.length] = debugger;
        listeners = temp;
    }


    public void removeBreakPointListener(BreakPointListener debugger) {
        BreakPointListener[] temp = new BreakPointListener[listeners.length - 1];
        System.arraycopy(listeners, 0, temp, 0, listeners.length - 1);
        for (int index = 0; index < listeners.length; index++) {
            if (index != temp.length && listeners[index].equals(debugger)) {
                temp[index] = listeners[listeners.length - 1];
                listeners = temp;
                return;
            }
        }
    }

}
