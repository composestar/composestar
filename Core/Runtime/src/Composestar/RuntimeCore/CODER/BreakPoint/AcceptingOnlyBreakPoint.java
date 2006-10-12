package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.*;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class AcceptingOnlyBreakPoint implements BreakPoint{

    public AcceptingOnlyBreakPoint() {
        super();
    }

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList MessageList, JoinPoint point){
        return eventType == DebuggerProvider.FILTER_ACCEPTED;
    }

    public boolean threadSpecific() {
        return false;
    }
}
