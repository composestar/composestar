package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class AcceptingOnlyBreakPoint extends BreakPoint {

    public AcceptingOnlyBreakPoint(Halter halt) {
        super(halt);
    }

    public boolean matchEvent(int eventType, FilterElementRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context){
        return eventType == DebuggerProvider.FILTER_ACCEPTED;
    }

    public boolean threadSpecific() {
        return false;
    }
}
