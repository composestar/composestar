package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;


import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for DummyBreakpoint.
 */
public class NeverBreakBreakPoint implements BreakPoint{
    public NeverBreakBreakPoint() {
    }

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point){
		return false;
    }
}
