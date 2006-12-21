package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;

/**
 * Summary description for MultiBreakPoint.
 */
public class MultiBreakPoint implements BreakPoint{
    private ArrayList breakpoints;

    public MultiBreakPoint() {
        breakpoints = new ArrayList();
    }

    public MultiBreakPoint(MultiBreakPoint breakpoint) {
        super();
        addBreakpoint(breakpoint);
    }

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point){
        Iterator i = breakpoints.iterator();
        while (i.hasNext()) {
            if (((BreakPoint) i.next()).matchEvent(eventType, currentFilter, messageList, point)) return true;
        }
        return false;
    }

    public void addBreakpoint(BreakPoint point) {
        breakpoints.add(point);
    }

    public BreakPoint add(BreakPoint point) {
        addBreakpoint(point);
        return this;
    }

    public BreakPoint remove(BreakPoint point) {
        breakpoints.remove(point);
        return this;
    }
}
