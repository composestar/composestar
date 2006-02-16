package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Model.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;

/**
 * Summary description for MultiBreakPoint.
 */
public class MultiBreakPoint extends BreakPoint {
    private boolean threadSpecifics = false;
    private ArrayList breakpoints;

    public MultiBreakPoint(Halter halt) {
        super(halt);
        breakpoints = new ArrayList();
    }

    public MultiBreakPoint(Halter halt, MultiBreakPoint breakpoint) {
        this(halt);
        addBreakpoint(breakpoint);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context) {

        Iterator i = breakpoints.iterator();
        while (i.hasNext()) {
            if (((BreakPoint) i.next()).matchEvent(eventType, currentFilter, source, message, target, filters, context)) return true;
        }
        return false;
    }

    protected BreakPoint getCopy() {
        return threadSpecific() ? new MultiBreakPoint(halt, this) : this;
    }

    public void addBreakpoint(BreakPoint point) {
		if(point == null) return;

        if (point instanceof NeverBreakBreakPoint) {
        } else if (point instanceof MultiBreakPoint) {
            MultiBreakPoint mbpoint = (MultiBreakPoint) point;
            threadSpecifics = mbpoint.threadSpecifics || threadSpecifics;
            Iterator i = mbpoint.breakpoints.iterator();
            while (i.hasNext()) {
                BreakPoint item = ((BreakPoint) i.next()).getForNextThread();
                breakpoints.add(item);
            }
        } else {
            threadSpecifics = threadSpecifics || point.threadSpecific();
            breakpoints.add(point);
        }
    }

    // Override when breakpoint uses information from multiple threads.
    // e.g. PI Calculus instead of LTL or Regular expressions
    public boolean threadSpecific() {
        return threadSpecifics;
    }

    public BreakPoint add(BreakPoint point) {
        addBreakpoint(point);
        return this;
    }

    public BreakPoint remove(BreakPoint point) {
        if (super.remove(point).equals(this)) return this;
        threadSpecifics = false;
        breakpoints.remove(point);
        Iterator i = breakpoints.iterator();
        while (!threadSpecifics && i.hasNext()) {
            BreakPoint item = ((BreakPoint) i.next()).getForNextThread();
            threadSpecifics = item.threadSpecific() || threadSpecifics;
        }
        return this;
    }
}
