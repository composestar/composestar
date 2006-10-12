package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;

import java.util.*;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class SenderBreakPoint extends ObjectBreakPoint {

    public SenderBreakPoint(Halter halt,String targetList) throws BreakPointParseException {
        super(halt,targetList);
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context){
		LinkedList list = afterMessage.getMessages();
		for(int i = 0; i < list.size();i++)
		{
			if(matchObject(((DebuggableMessage)list.get(i)).getSender()))
			{
				return true;
			}
		}
		return false;
    }
}
