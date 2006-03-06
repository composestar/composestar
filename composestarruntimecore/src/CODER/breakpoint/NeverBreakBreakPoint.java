package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessageList;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for DummyBreakpoint.
 */
public class NeverBreakBreakPoint extends BreakPoint {
    public NeverBreakBreakPoint(Halter halt) {
        super(halt);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessageList message, Object target, ArrayList filters, Dictionary context) {
        return false;
    }

    public boolean threadSpecific() {
        return false;
    }
}
