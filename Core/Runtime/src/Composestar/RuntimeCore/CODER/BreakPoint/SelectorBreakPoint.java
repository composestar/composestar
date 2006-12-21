package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.FLIRT.*;
import Composestar.RuntimeCore.FLIRT.Reflection.JoinPoint;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;

import java.util.*;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public class SelectorBreakPoint implements BreakPoint{

	String sList = "";

    public SelectorBreakPoint(String targetList) throws BreakPointParseException {
		this.sList = targetList;
    }

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point){
		/*LinkedList list = afterMessage.getMessages();
		for(int i = 0; i < list.size();i++)
		{
			if(sList.indexOf((list.get(i)).getSelector()) >= 0)
			{
				return true;
			}
		}*/
		return false;
    }
}
