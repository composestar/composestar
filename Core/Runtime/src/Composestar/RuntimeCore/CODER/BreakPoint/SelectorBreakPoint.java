package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;

import java.util.*;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class SelectorBreakPoint extends BreakPoint{

	String sList = "";

    public SelectorBreakPoint(Halter halt,String targetList) throws BreakPointParseException {
        super(halt);
		this.sList = targetList;
    }

    public boolean matchEvent(int eventType, DebuggableFilter currentFilter, MessageList beforeMessage, MessageList afterMessage, ArrayList filters, Dictionary context){
		LinkedList list = afterMessage.getMessages();
		for(int i = 0; i < list.size();i++)
		{
			if(sList.indexOf(((DebuggableSingleMessage)list.get(i)).getSelector()) >= 0)
			{
				return true;
			}
		}
		return false;
    }
}
