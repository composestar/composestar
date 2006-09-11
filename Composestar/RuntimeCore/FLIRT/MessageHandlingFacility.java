package Composestar.RuntimeCore.FLIRT;

import Composestar.RuntimeCore.FLIRT.JoinPoint.*;
import Composestar.RuntimeCore.FLIRT.Message.*;
import Composestar.RuntimeCore.Utils.*;

public abstract class MessageHandlingFacility {
	public static void handleInstanceCreation(Object sender, Object target, Object[] arguments)
	{
		Message msg = MessagePool.getMessage(sender,target,"",arguments,Message.MESSAGE_CONSTRUCTOR);
		deliver(msg);
	}
	
	public static void handleVoidMethodCall(Object sender, Object target, String selector, Object[] arguments)
	{
		Message msg = MessagePool.getMessage(sender,target,selector,arguments,Message.MESSAGE_NONSTATIC_NONSTATIC_VOID);
		deliver(msg);
	}
	
	public static Object handleReturnMethodCall(Object sender, Object target, String selector, Object[] arguments)
	{
		Message msg = MessagePool.getMessage(sender,target,selector,arguments,Message.MESSAGE_NONSTATIC_NONSTATIC_RETURN);
		msg = deliver(msg);
		return msg.getResponse();
	}
	
	public synchronized static void handleApplicationStart(String filename, int debug, PlatformProvider provider)
	{
		Debug.setMode(debug);
		provider.instantiatePlatform();
	}
	
	protected static Message deliver(Message message)
	{
		JoinPoint.evaluateMessage(message);
		return message;
	}
}
