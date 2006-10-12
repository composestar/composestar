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
public class SenderBreakPoint extends ObjectBreakPoint {

    public SenderBreakPoint(String targetList) throws BreakPointParseException {
        super(targetList);
    }

    public boolean matchEvent(int eventType, FilterRuntime currentFilter, MessageList messageList, JoinPoint point){
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
