package Composestar.RuntimeCore.CODER.ConsoleDebugger;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.BreakPoint.AlwaysBreakBreakPoint;
import Composestar.RuntimeCore.CODER.Debugger;
import Composestar.RuntimeCore.CODER.DebuggerProvider;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessageList;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Just prints debug information like the system normaly does
 */
public class ConsolePrinterDebugger implements Debugger, BreakPointListener {
    private DebuggerProvider provider = null;

    public ConsolePrinterDebugger(DebuggerProvider provider) {
        this.provider = provider;
    }

    public void start() {
        provider.HaltRuntime();
        provider.clearBreakPoints();
        Halter halt = provider.getHalter();
        provider.addBreakPoint(new AlwaysBreakBreakPoint(halt));
        provider.addBreakPointListener(this);
        provider.ResumeRuntime();
    }

    public void stop() {
        provider.HaltRuntime();
        provider.clearBreakPoints();
        provider.removeBreakPointListener(this);
        provider.ResumeRuntime();
    }

    public void reset() {
        provider.HaltRuntime();
        provider.clearBreakPoints();
        provider.removeBreakPointListener(this);
        provider.addBreakPointListener(this);
        Halter halt = provider.getHalter();
        provider.addBreakPoint(new AlwaysBreakBreakPoint(halt));
        provider.ResumeRuntime();
    }

    public void breakEvent(int eventType, StateHandler handler,DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context) {
	    switch (eventType) {
            case DebuggerProvider.FILTER_ACCEPTED:
                Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilter is accepted.");
                break;
            case DebuggerProvider.FILTER_REJECTED:
                Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFilter is rejected.");
                break;
            default:
                break;
        }
    }
}
