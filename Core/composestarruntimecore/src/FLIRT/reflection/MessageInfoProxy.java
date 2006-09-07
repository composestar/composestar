package Composestar.RuntimeCore.FLIRT.Reflection;

import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.Utils.ChildThread;

public class MessageInfoProxy
{
	public static void updateMessage(Message msg)
	{ 
		MessageInfo.updateMessage(msg);
	}

	public static void updateMessage(ChildThread t, Message msg)
	{ 
		MessageInfo.updateMessage(t, msg);
	}

	public static void updateMessage(MessageList msg)
	{ 
		updateMessage(msg.getOrgMessage());
	}

	public static void updateMessage(ChildThread t, MessageList msg)
	{ 
		MessageInfo.updateMessage(t, msg.getOrgMessage());
	}
}
