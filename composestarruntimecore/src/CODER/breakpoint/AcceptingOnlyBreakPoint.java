package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessageList;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class AcceptingOnlyBreakPoint extends BreakPoint {

    public AcceptingOnlyBreakPoint(Halter halt) {
        super(halt);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context){
        return eventType == DebuggerProvider.FILTER_ACCEPTED;
    }

    public boolean threadSpecific() {
        return false;
    }
}
