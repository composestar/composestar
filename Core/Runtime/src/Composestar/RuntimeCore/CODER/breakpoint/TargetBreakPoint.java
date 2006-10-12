package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;

import java.util.*;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class TargetBreakPoint extends ObjectBreakPoint {

    public TargetBreakPoint(Halter halt,String targetList) throws BreakPointParseException{
        super(halt,targetList);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, DebuggableMessageList beforeMessage, DebuggableMessageList afterMessage, ArrayList filters, Dictionary context){
		LinkedList list = afterMessage.getMessages();
		for(int i = 0; i < list.size();i++)
		{
			if(matchObject(((DebuggableSingleMessage)list.get(i)).getTarget()))
			{
				return true;
			}
		}
		return false;
    }
}
