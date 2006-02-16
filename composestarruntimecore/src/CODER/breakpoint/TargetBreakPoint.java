package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.VisualDebugger.Parsers.BreakPointParseException;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class TargetBreakPoint extends ObjectBreakPoint {

    public TargetBreakPoint(Halter halt,String targetList) throws BreakPointParseException{
        super(halt,targetList);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context) {
        return matchObject(target);
    }
}
