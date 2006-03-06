package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class SelectorBreakPoint extends ObjectBreakPoint {

    public SelectorBreakPoint(Halter halt,String targetList) throws BreakPointParseException {
        super(halt,targetList);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context) {
        return matchObject(source);
    }
}
