package Composestar.RuntimeCore.FLIRT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernReference;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.RuntimeCore.FLIRT.Exception.ComposestarRuntimeException;
import Composestar.RuntimeCore.FLIRT.Exception.ErrorFilterException;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Reflection.MessageInfoProxy;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * MessageHandlingFacility.java 2072 2006-10-15 12:44:56Z elmuerte $ This class
 * handles the intercepted messages and directs them to the rest of FLIRT
 */
public abstract class MessageHandlingFacility
{
	protected static DataStore datastore = null;

	private static final Object[] EmptyObjectArray = {};

	/**
	 * @roseuid 40EA969503AB
	 */
	public MessageHandlingFacility()
	{}

	/**
	 * Instance creation from a nonstatic context
	 * 
	 * @param createdObject
	 * @param args
	 * @param creator
	 */
	public synchronized static void handleInstanceCreation(Object creator, Object createdObject, Object[] args)
	{
		String shortname = createdObject.getClass().getName();
		shortname = shortname.substring(shortname.lastIndexOf(".") + 1); // get
		// the
		// class
		// name
		// only
		Message msg = new Message(shortname /* createdObject.GetType().get_Name() */, args);
		msg.setSender(creator);
		msg.setTarget(createdObject);
		handleInstanceCreation(msg, creator, createdObject, args);
	}

	/**
	 * Instance creation from a static context WARNING THIS METHOD IS NONENDING
	 * RECURSIVE
	 * 
	 * @param args
	 * @param createdObject
	 * @param staticcontext
	 */
	public synchronized static void handleInstanceCreation(String staticcontext, Object createdObject, Object[] args)
	{
		// this used to be GetType()
		handleInstanceCreation(createdObject.getClass(), createdObject, args);
	}

	private static void handleInstanceCreation(Message message, Object creator, Object createdObject, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("instance creation", creator.toString(), createdObject.getClass().getName(), message
					.getSelector(), args);
		}
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_DEBUG, "FLIRT", "\tMap: " + RepositoryLinker.filterModuleReferenceMap);
		}

		HashMap mapping = new HashMap();

		CpsConcern cpsConcern = null;

		// First get the object args and get the formal parameters from the
		// concern
		if (datastore.getObjectByID(createdObject.getClass().getName()) instanceof CpsConcern)
		{
			cpsConcern = (CpsConcern) datastore.getObjectByID(createdObject.getClass().getName());
		}
		// HACK: we need to see of it is a cpsconcern, however full namespaces
		// for concerns are not supported :(
		// warning this will not work with the same names for concerns in
		// multiple packages!
		else if (createdObject.getClass().getName().indexOf(".") > 0)
		{
			// if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","Found
			// full namespace '"+createdObject.getClass().getName()+"'.");
			String tmp = createdObject.getClass().getName().substring(
					createdObject.getClass().getName().lastIndexOf(".") + 1);
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(1): "+tmp);
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(2):
			// "+datastore.getObjectByID(tmp));
			if (datastore.getObjectByID(tmp) != null && datastore.getObjectByID(tmp) instanceof CpsConcern)
			{
				CpsConcern cc = (CpsConcern) datastore.getObjectByID(tmp);
				// if(Debug.SHOULD_DEBUG)
				// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(3):
				// "+cc.getImplementation().repositoryKey);
				if (cc.getImplementation() instanceof Source)
				{
					Source tmp_src = (Source) cc.getImplementation();
					// if(Debug.SHOULD_DEBUG)
					// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(4a):
					// "+tmp_src.getClassName());
					if (createdObject.getClass().getName().equals(tmp_src.getClassName()))
					{
						// Now we are sure!
						// if(Debug.SHOULD_DEBUG)
						// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(5a): Found
						// correct concern!");
						cpsConcern = cc;
					}
				}
				else if (cc.getImplementation() instanceof CompiledImplementation)
				{
					CompiledImplementation tmp_ci = (CompiledImplementation) cc.getImplementation();
					// if(Debug.SHOULD_DEBUG)
					// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(4b):
					// "+tmp_ci.getClassName());
					if (createdObject.getClass().getName().equals(tmp_ci.getClassName()))
					{
						// Now we are sure!
						// if(Debug.SHOULD_DEBUG)
						// Debug.out(Debug.MODE_DEBUG,"FLIRT","step(5b): Found
						// correct concern!");
						cpsConcern = cc;
					}
				}
			}
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_DEBUG,"FLIRT","\n\n\n\n");
		}

		if (cpsConcern != null)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound CPS concern '" + cpsConcern + "'.");
			}
			Iterator paramIterator = cpsConcern.getParameterIterator();
			int i = 0;
			while (paramIterator.hasNext())
			{
				LabeledConcernReference lcr = (LabeledConcernReference) paramIterator.next();
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tProcessing argument " + i + " of " + args.length
							+ "...");
				}
				if (i < args.length)
				{
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tChecking argument["
								+ args[i].getClass().getName().equals(lcr.getQualifiedName()) + "]: "
								+ args[i].getClass().getName() + " == " + lcr.getQualifiedName());
					}
					if (args[i].getClass().getName().equals(lcr.getQualifiedName()))
					{
						if (Debug.SHOULD_DEBUG)
						{
							Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tFound name: " + lcr.getName());
							Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tFound name: " + lcr.getQualifiedName());
							Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tFound name: " + lcr.getLabel());
						}
						mapping.put(lcr.getLabel(), args[i]);
					}
					else
					{
						throw new ComposestarRuntimeException("Called constructor of concern '" + cpsConcern.getName()
								+ "' does not match the specified one.");
					}
				}
				else
				{
					throw new ComposestarRuntimeException("Called constructor of concern '" + cpsConcern.getName()
							+ "' does not match the specified one.");
				}
				i++;
			}
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_DEBUG, "FLIRT", "\tConstructor map: " + mapping);
			}
			Iterator fmIterator = cpsConcern.getFilterModuleIterator();
			while (fmIterator.hasNext())
			{
				FilterModule exfm = (FilterModule) fmIterator.next();
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tFound external filtermodule '"
							+ cpsConcern.getName() + "." + exfm.getName() + "'.");
				}
				FilterModuleRuntime exfmr = (FilterModuleRuntime) RepositoryLinker.filterModuleReferenceMap
						.get(cpsConcern.getName() + "." + exfm.getName());
				if (exfmr != null)
				{
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tFound FilterModuleRuntime '" + exfmr + "'.");
					}
					/*
					 * Iterator keys = mapping.keySet().iterator();
					 * while(keys.hasNext()) { String key = (String)keys.next();
					 * if(Debug.SHOULD_DEBUG)
					 * Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\tAdding
					 * external '"+key+"'.");
					 * exfmr.addExternal(key,mapping.get(key)); }
					 */
					Iterator entries = mapping.entrySet().iterator();
					while (entries.hasNext())
					{
						Entry entry = (Entry) entries.next();
						if (Debug.SHOULD_DEBUG)
						{
							Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tAdding external '" + entry.getKey() + "'.");
						}
						exfmr.addExternal((String) entry.getKey(), entry.getValue());
					}
				}
			}
		}
		/*
		 * else { throw new ComposestarRuntimeException("The called constructor
		 * of the concern: "+createdObject.getClass().getName()+" does not match
		 * the specified one."); }
		 */

		ObjectManager.getObjectManagerFor(createdObject, datastore);

		message.STATE = Message.MESSAGE_CONSTRUCTOR;
		MessageHandlingFacility.handleConstructorCall(creator, createdObject, message);

		// Update the message!!!
		MessageInfoProxy.updateMessage(message);
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
	}

	private static void handleConstructorCall(Object creator, Object createdObject, Message msg)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming constructor message", creator.getClass().getName(), msg.getTarget()
					.getClass().toString(), msg.getSelector(), EmptyObjectArray);
		}

		// First check do the output filters
		// ObjectManager om = ObjectManager.getObjectManagerFor(creator,
		// datastore);
		// msg = (Message) om.deliverOutgoingMessage(creator,createdObject,msg);

		// Now check for input filters
		msg.setDirection(Message.INCOMING);
		ObjectManager om = ObjectManager.getObjectManagerFor(createdObject, datastore);

		try
		{
			om.deliverIncomingMessage(creator, createdObject, msg);
		}
		catch (ErrorFilterException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Rethrowing ErrorFilterException");
			}
			throw new ErrorFilterException(e);
		}
		catch (ComposestarRuntimeException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "FLIRT", "Rethrowing ComposestarRuntimeException");
			}
			throw new ComposestarRuntimeException(e);
		}

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
	}

	/**
	 * This method is called when a void method call is received from a static
	 * context nonstatic -> nonstatic with return
	 * 
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 * @param caller
	 */
	public static Object handleReturnMethodCall(Object caller, Object target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming non static -> non static return message", caller.getClass().getName(),
					target.getClass().toString(), selector, args);
		}
		Message msg = new Message(selector, args);
		msg.setSender(caller);
		msg.setServer(target);
		msg.STATE = Message.MESSAGE_NONSTATIC_NONSTATIC_RETURN;
		ObjectManager om;
		Object returnvalue;

		try
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_DEBUG, "FLIRT", ">>> OUTGOING: " + msg);
			}
			if (ObjectManager.hasFilterModules(caller, datastore))
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"Filtermodule(s) present at caller, delivering message...");
				}
				om = ObjectManager.getObjectManagerFor(caller, datastore);
				msg = (Message) om.deliverOutgoingMessage(caller, target, msg);
			}
			else
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug
							.out(Debug.MODE_INFORMATION, "FLIRT",
									"No filtermodules present at caller, sending message...");
				}
				msg.setDirection(Message.INCOMING);
				msg.setTarget(target);
			}

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_DEBUG, "FLIRT", ">>> INCOMING: " + msg);
			}
			if (ObjectManager.hasFilterModules(msg.getTarget(), datastore))
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"Filtermodule(s) present at target, delivering message...");
				}
				om = ObjectManager.getObjectManagerFor(msg.getTarget(), datastore);
				returnvalue = om.deliverIncomingMessage(caller, msg.getTarget(), msg);
			}
			else
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"No filtermodules present at target, invoking message...");
				}
				returnvalue = Invoker.getInstance().invoke(msg.getTarget(), msg.getSelector(), msg.getArguments());
			}
		}
		catch (ErrorFilterException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Rethrowing ErrorFilterException");
			}
			throw new ErrorFilterException(e);
		}
		catch (ComposestarRuntimeException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "FLIRT", "Rethrowing ComposestarRuntimeException");
			}
			throw new ComposestarRuntimeException(e);
		}

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
		return returnvalue;
	}

	/**
	 * This method is called when a void method call is received nonstatic ->
	 * nonstatic without return
	 * 
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 * @param caller
	 */
	public static void handleVoidMethodCall(Object caller, Object target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming non static -> non static void message", caller.getClass().getName(),
					target.getClass().toString(), selector, args);
		}

		Message msg = new Message(selector, args);
		msg.setSender(caller);
		msg.setServer(target);
		msg.STATE = Message.MESSAGE_NONSTATIC_NONSTATIC_VOID;
		ObjectManager om;

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_DEBUG, "FLIRT", ">>> OUTGOING: " + msg);
		}

		try
		{
			if (ObjectManager.hasFilterModules(caller, datastore))
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"Filtermodule(s) present at caller, delivering message...");
				}
				om = ObjectManager.getObjectManagerFor(caller, datastore);
				msg = (Message) om.deliverOutgoingMessage(caller, target, msg);
			}
			else
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug
							.out(Debug.MODE_INFORMATION, "FLIRT",
									"No filtermodules present at caller, sending message...");
				}
				msg.setDirection(Message.INCOMING);
				msg.setTarget(target);
			}

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_DEBUG, "FLIRT", ">>> INCOMING: " + msg);
			}
			if (ObjectManager.hasFilterModules(msg.getTarget(), datastore))
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"Filtermodule(s) present at target, delivering message...");
				}
				om = ObjectManager.getObjectManagerFor(msg.getTarget(), datastore);
				om.deliverIncomingMessage(caller, msg.getTarget(), msg);
			}
			else
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"No filtermodules present at target, invoking message...");
				}
				Invoker.getInstance().invoke(msg.getTarget(), msg.getSelector(), msg.getArguments());
			}
		}
		catch (ErrorFilterException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Rethrowing ErrorFilterException");
			}
			throw new ErrorFilterException(e);
		}
		catch (ComposestarRuntimeException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "FLIRT", "Rethrowing ComposestarRuntimeException");
			}
			throw new ComposestarRuntimeException(e);
		}

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
	}

	/**
	 * This method is called when a return method call is received from a static
	 * context static -> nonstatic with return
	 * 
	 * @param staticcaller The class which initiated the call
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 */
	public static Object handleReturnMethodCall(String staticcaller, Object target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming static -> non static return message", staticcaller, target.getClass()
					.getName(), selector, args);
		}
		Message msg = new Message(selector, args);
		msg.setSender(staticcaller);
		msg.setServer(target);
		msg.STATE = Message.MESSAGE_STATIC_NONSTATIC_RETURN;
		msg.setDirection(Message.INCOMING);
		Object returnvalue;

		try
		{
			if (ObjectManager.hasFilterModules(target, datastore))
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"Filtermodule(s) present at target, delivering message...");
				}
				ObjectManager om = ObjectManager.getObjectManagerFor(target, datastore);
				returnvalue = om.deliverIncomingMessage(staticcaller.getClass().getName(), target, msg);
			}
			else
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"No filtermodules present at target, invoking message...");
				}
				returnvalue = Invoker.getInstance().invoke(target, selector, args);
			}
		}
		catch (ErrorFilterException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Rethrowing ErrorFilterException");
			}
			throw new ErrorFilterException(e);
		}
		catch (ComposestarRuntimeException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "FLIRT", "Rethrowing ComposestarRuntimeException");
			}
			throw new ComposestarRuntimeException(e);
		}

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
		return returnvalue;
	}

	/**
	 * This method is called when a void method call is received from a static
	 * context static -> nonstatic without return
	 * 
	 * @param staticcaller The class which initiated the call
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 * @roseuid 40EA966800B7
	 */
	public static void handleVoidMethodCall(String staticcaller, Object target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming static -> non static void message", staticcaller, target.getClass()
					.getName(), selector, args);
		}

		try
		{
			if (ObjectManager.hasFilterModules(target, datastore))
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"Filtermodule(s) present at target, delivering message...");
				}
				Message msg = new Message(selector, args);
				msg.setSender(staticcaller);
				msg.setServer(target);
				msg.STATE = Message.MESSAGE_STATIC_NONSTATIC_VOID;
				msg.setDirection(Message.INCOMING);
				ObjectManager om = ObjectManager.getObjectManagerFor(target, datastore);
				om.deliverIncomingMessage(null, target, msg);
			}
			else
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT",
							"No filtermodules present at target, invoking message...");
				}
				Invoker.getInstance().invoke(target, selector, args);
			}
		}
		catch (ErrorFilterException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Rethrowing ErrorFilterException");
			}
			throw new ErrorFilterException(e);
		}
		catch (ComposestarRuntimeException e)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "FLIRT", "Rethrowing ComposestarRuntimeException");
			}
			throw new ComposestarRuntimeException(e);
		}

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
	}

	/**
	 * This method is called when a void method call is received from a static
	 * context nonstatic -> static with return
	 * 
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 * @param caller
	 */
	public static Object handleReturnMethodCall(Object caller, String target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming non static -> static return message", caller.getClass().getName(), target,
					selector, args);
		}
		Object returnvalue;

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Invoking message...");
		}
		returnvalue = Invoker.getInstance().invoke(target, selector, args);

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
		return returnvalue;
	}

	/**
	 * This method is called when a void method call is received nonstatic ->
	 * static without return
	 * 
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 * @param caller
	 */
	public static void handleVoidMethodCall(Object caller, String target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming non static -> static void message", caller.getClass().getName(), target,
					selector, args);
		}

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Invoking message...");
		}
		Invoker.getInstance().invoke(target, selector, args);

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
	}

	/**
	 * This method is called when a return method call is received from a static
	 * context static -> static with return
	 * 
	 * @param staticcaller The class which initiated the call
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 */
	public static Object handleReturnMethodCall(String staticcaller, String target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming static -> static return message", staticcaller, target, selector, args);
		}

		Object returnvalue;
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Invoking message...");
		}
		returnvalue = Invoker.getInstance().invoke(target, selector, args);

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
		return returnvalue;
	}

	/**
	 * This method is called when a void method call is received from a static
	 * context static -> static without return
	 * 
	 * @param staticcaller The class which initiated the call
	 * @param target The object to which the call is made
	 * @param selector The call
	 * @param args The arguments of the call
	 * @roseuid 40EA966800B7
	 */
	public static void handleVoidMethodCall(String staticcaller, String target, String selector, Object[] args)
	{
		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodStart("incoming static -> static void message", staticcaller, target, selector, args);
		}

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Invoking message...");
		}
		Invoker.getInstance().invoke(target, selector, args);

		if (Debug.SHOULD_DEBUG)
		{
			logIncomingMethodEnd();
		}
	}

	private static void logIncomingMethodStart(String mdefinition, String caller, String target, String selector,
			Object[] args)
	{
		// if(Debug.SHOULD_DEBUG)
		// {
		Debug.out(Debug.MODE_INFORMATION, "FLIRT",
				"*********************************************************************");
		Debug.out(Debug.MODE_INFORMATION, "FLIRT", "MessageHandlingFacility: Handling " + mdefinition + "...");
		Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFrom caller: " + caller);
		Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFor target: " + target);
		Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tWith selector: " + selector);
		Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tMessage has " + args.length + " argument(s).");
		for (int i = 0; i < args.length; i++)
		{
			// Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\tArgument["+i+"] =
			// "+args[i]);
		}
		// }
	}

	private static void logIncomingMethodEnd()
	{
		// if(Debug.SHOULD_DEBUG)
		Debug.out(Debug.MODE_INFORMATION, "FLIRT",
				"*********************************************************************");
	}

	/**
	 * When the application starts this method is called to deserialize and link
	 * the compile time structure to the runtime
	 * 
	 * @param filename String The location of the xml file
	 * @param debug int The debug level $param debugInterface boolean Turn on
	 *            the debugger interface
	 * @param provider
	 */
	public synchronized static void handleApplicationStart(String filename, int debug, PlatformProvider provider)
	{
		Debug.setMode(debug);

		provider.instantiatePlatform();

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "MessageHandlingFacility for application started...");
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Starting filter debugger providers");

			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Starting Filter Debugger");

			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Filter Debugger Started");
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Deserializing compile time structure from '" + filename
					+ "'...");
		}

		RepositoryDeserializer rd = provider.getRepositoryDeserializer();
		datastore = rd.deserialize(filename);
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Linking compile time structure...");
		}
		try
		{
			RepositoryLinker rl = new RepositoryLinker(datastore);
			rl.link();
		}
		catch (Exception e)
		{
			if (Debug.getMode() > 1)
			{
				e.printStackTrace();
			}
			Debug.out(Debug.MODE_ERROR, "FLIRT", "Unable to link the Compile time to the Runtime shadow structure!");
			Debug.out(Debug.MODE_ERROR, "FLIRT", "Exiting...");
			System.exit(-1);
		}
	}
}