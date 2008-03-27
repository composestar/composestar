package Composestar.RuntimeCore.FLIRT.Reflection;

import java.util.WeakHashMap;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * <pre>
 *   This file is part of Composestar project [http://composestar.sf.net].
 *   Copyright (C) 2003 University of Twente.
 *   Licensed under LGPL v2.1 or (at your option) any later version.
 *   [http://www.fsf.org/copyleft/lgpl.html]
 *   $Id$
 * </pre>
 * 
 * MessageInfo provides an interface to access reflective information about the
 * current context; this is represented as a model of the message that caused
 * the execution of the current code. Through this interface, programmers can
 * refer to information such the sender, receiver or arguments of a message. The
 * MessageInfo class is intended to be used within a method or condition that is
 * part of a concern (i.e. it has superimposed filters). In other cases, e.g. in
 * methods that have not dispatched by filters, the MessageInfo interface does
 * not provide correct information.
 * <p>
 * Each getter method returns an instance of Object. Therefore, it may be
 * necessary to cast this instance to its direct type. The following piece of
 * code shows a simple example of the use of MessageInfo:
 * 
 * <pre>
 *  
 *     ...
 *     void foo(){
 *        Bar bar=null;
 *  
 *        Object sender = Composestar.Runtime.FLIRT.message.MessageInfo.getMessageInfo().getSender();
 *  
 *        if (sender instanceof Bar)
 *           bar=(Bar)sender;
 *  		...
 *        
 *     }
 *   
 * </pre>
 */

public class MessageInfo
{
	// Message for real application threads will stay around until the next
	// message is processed. This produces a minor memory leak.
	private static WeakHashMap messagesByThread = new WeakHashMap();

	/**
	 * Returns the actual MessageInfo instance for the current thread.
	 * 
	 * @return Composestar.Runtime.FLIRT.message.MessageInfo
	 */

	public static Message getMessageInfo()
	{
		return (Message) messagesByThread.get(Thread.currentThread());
	}

	/**
	 * Updates the message for this thread. This method should be called only
	 * within the interpreter
	 * 
	 * @param msg
	 */

	static void updateMessage(Message msg)
	{
		messagesByThread.put(Thread.currentThread(), new Message(msg));
	}

	static void updateMessage(Thread t, Message msg)
	{
		messagesByThread.put(t, new Message(msg));
	}

	/**
	 * Returns the base object that is the subject of the superimposition.
	 * 
	 * @return java.lang.Object
	 */
	public static Object getInner()
	{
		return MessageInfo.getMessageInfo().getInner();
	}

	/**
	 * Returns the object that sent the message.
	 * 
	 * @return java.lang.Object
	 */
	public static Object getSender()
	{
		return MessageInfo.getMessageInfo().getSender();
	}

	/**
	 * Returns the arguments of the message.
	 * 
	 * @return java.lang.Object[]
	 */
	public static Object[] getArguments()
	{
		return MessageInfo.getMessageInfo().getArguments();
	}

	/**
	 * Returns the selector of the message.
	 * 
	 * @return java.lang.Object[]
	 */
	public static Object getSelector()
	{
		return MessageInfo.getMessageInfo().getSelector();
	}

	/**
	 * Returns the instance of the superimposed concern.
	 */
	public static Object getServer()
	{
		return MessageInfo.getMessageInfo().getServer();
	}
}
