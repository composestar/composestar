package Composestar.RuntimeCore.CODER.ConsoleDebugger;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.BreakPoint.AlwaysBreakBreakPoint;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Just prints debug information like the system normaly does
 */
public class ConsolePrinterDebugger implements Debugger {
    public ConsolePrinterDebugger() {
    }

    public void start() {
    }

    public void stop() {
    }

    public void reset() {
    }

    public void event(int eventType, StateHandler handler, FilterRuntime currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context) {
	    switch (eventType) {
            case Debugger.FILTER_ACCEPTED:
                Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilter is accepted.");
                break;
            case Debugger.FILTER_REJECTED:
                Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilter is rejected.");
                break;
            default:
                break;
        }
    }
}
