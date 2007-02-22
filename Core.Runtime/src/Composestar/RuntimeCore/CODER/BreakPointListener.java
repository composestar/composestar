package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for BreakPointListener.
 */
public interface BreakPointListener {
    public void breakEvent(int eventType, StateHandler handler,FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context);
}
