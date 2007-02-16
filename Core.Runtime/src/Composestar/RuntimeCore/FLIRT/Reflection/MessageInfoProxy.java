package Composestar.RuntimeCore.FLIRT.Reflection;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import java.lang.Thread;

public class MessageInfoProxy
{
	public static void updateMessage(Message msg)
	{
		MessageInfo.updateMessage(msg);
	}

	public static void updateMessage(Thread t, Message msg)
	{
		MessageInfo.updateMessage(t, msg);
	}

	public static void updateMessage(MessageList msg)
	{
		updateMessage(msg.getOrgMessage());
	}

	public static void updateMessage(Thread t, MessageList msg)
	{
		MessageInfo.updateMessage(t, msg.getOrgMessage());
	}
}
