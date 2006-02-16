package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for BreakPointListener.
 */
public interface BreakPointListener {
    public void breakEvent(int eventType, StateHandler handler,DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context);
}
