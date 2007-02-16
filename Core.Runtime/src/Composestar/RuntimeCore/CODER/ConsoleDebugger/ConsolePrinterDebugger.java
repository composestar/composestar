package Composestar.RuntimeCore.CODER.ConsoleDebugger;

import Composestar.RuntimeCore.CODER.DebuggerProvider;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Filtertypes.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.*;
import Composestar.RuntimeCore.FLIRT.Reflection.*;
import Composestar.RuntimeCore.FLIRT.Debugger.Debugger;

import Composestar.RuntimeCore.Utils.Debug;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Just prints debug information like the system normaly does
 */
public class ConsolePrinterDebugger extends DebuggerProvider {
    public void executeEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point) {
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
