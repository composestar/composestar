package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for BreakPoint.
 */
public interface BreakPoint {
    public abstract boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filtersModules, Dictionary context);
}
