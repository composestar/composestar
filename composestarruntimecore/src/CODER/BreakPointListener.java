package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessageList;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for BreakPointListener.
 */
public interface BreakPointListener {
    public void breakEvent(int eventType, StateHandler handler,DebuggableFilter currentFilter, Object source, DebuggableMessageList message, Object target, ArrayList filters, Dictionary context);
}
