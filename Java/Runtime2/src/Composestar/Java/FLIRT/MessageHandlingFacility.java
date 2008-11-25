/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Java.FLIRT;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.Signatures.MethodInfoWrapper;
import Composestar.Core.LAMA.Signatures.MethodRelation;
import Composestar.Core.LAMA.Signatures.MethodStatus;
import Composestar.Core.LAMA.Signatures.Signature;
import Composestar.Java.FLIRT.Env.MessageDirection;
import Composestar.Java.FLIRT.Env.MessageState;
import Composestar.Java.FLIRT.Env.ObjectManager;
import Composestar.Java.FLIRT.Env.RTCpsObject;
import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Env.SimpleCpsObject;
import Composestar.Java.FLIRT.Utils.Invoker;

/**
 * Entry point for the filter runtime runtime
 * 
 * @author Michiel Hendriks
 */
public final class MessageHandlingFacility
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.MODULE_NAME);

	public static final boolean RT_DEBUG = true || Boolean.getBoolean("composestar.runtime.debug");

	/**
	 * The CPS repository, initialized on startup
	 */
	private static Repository repository;

	private MessageHandlingFacility()
	{}

	/**
	 * Initialize the runtime environment
	 */
	public static synchronized void initialize(Repository repos)
	{
		repository = repos;
	}

	/**
	 * Set the logging debug output
	 * 
	 * @param lvl
	 */
	public static synchronized void setDebugLevel(int lvl)
	{
		lvl = Math.max(lvl, Integer.getInteger("composestar.runtime.loglevel", -1));
		if (lvl < 0)
		{
			logger.setLevel(Level.OFF);
			return;
		}
		switch (lvl)
		{
			case 0:
				// logger.setLevel(Level.OFF);
				// break;
			case 1:
				logger.setLevel(Level.SEVERE);
				break;
			case 2:
				logger.setLevel(Level.WARNING);
				break;
			case 3:
				logger.setLevel(Level.INFO);
				break;
			case 4:
				logger.setLevel(Level.CONFIG);
				break;
			case 5:
				logger.setLevel(Level.FINE);
				break;
			case 6:
				logger.setLevel(Level.FINER);
				break;
			case 7:
			default:
				logger.setLevel(Level.FINEST);
				break;
		}
	}

	/**
	 * When the application starts this method is called to deserialize and link
	 * the compile time structure to the runtime
	 * 
	 * @param filename String The location of the xml file
	 * @param debug int The debug level
	 * @param mainclass The main class name
	 */
	public static synchronized void handleApplicationStart(String filename, int debug, Class<?> mainclass)
	{
		setDebugLevel(debug);
		initialize(RepositoryLoader.load(filename, mainclass));
	}

	//
	// Various message handling routines
	//

	/**
	 * Instance creation from an instance context
	 * 
	 * @param creator
	 * @param createdObject
	 * @param args
	 */
	public static synchronized void handleInstanceCreation(Object creator, Object createdObject, Object[] args,
			String key)
	{
		internalInstanceCreation(creator, createdObject, args, key);
	}

	/**
	 * Instance creation from a static context
	 * 
	 * @param staticcontext
	 * @param createdObject
	 * @param args
	 */
	public static synchronized void handleInstanceCreation(String staticcontext, Object createdObject, Object[] args,
			String key)
	{
		internalInstanceCreation(null, createdObject, args, key);
	}

	/**
	 * Performs the real instance creation, this is to work around the ambiguity
	 * of the handleInstanceCreation method
	 * 
	 * @param creator
	 * @param createdObject
	 * @param args
	 * @param key
	 */
	private static void internalInstanceCreation(Object creator, Object createdObject, Object[] args, String key)
	{
		RTCpsObject sender = getRTCpsObject(creator);
		RTMessage msg = new RTMessage(sender);
		ObjectManager om = ObjectManagerHandler.getObjectManager(createdObject, repository);
		msg.setServer(om);
		CpsSelector sel = new CpsSelectorImpl(createdObject.getClass().getSimpleName());
		msg.setSelector(sel);
		msg.setArguments(args);
		msg.setState(MessageState.CONSTRUCTOR);

		msg.setDirection(MessageDirection.INCOMING);
		try
		{
			// TODO constructor message are currently not handled because the
			// message selector has a dubious value, what should the selector
			// value of a constructor message be?
			// om.deliverIncomingMessage(sender, om, msg);
		}
		catch (RuntimeException e)
		{
			if (!RT_DEBUG)
			{
				e.fillInStackTrace();
			}
			throw e;
		}

		// TODO MessageInfoProxy.updateMessage(message);
	}

	/**
	 * Generic method handling
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleMethodCall(Object caller, Object target, String selector, Object[] args, String key,
			EnumSet<MessageState> state)
	{
		ObjectManager targetOm = ObjectManagerHandler.getObjectManager(target, repository);
		ObjectManager senderOm = ObjectManagerHandler.getObjectManager(caller, repository);
		if (targetOm == null && senderOm == null)
		{
			// no superimposition
			// TODO find MethodInfo for the best result
			return Invoker.invoke(target, selector, args);
		}

		RTCpsObject senderObj;
		if (senderOm == null)
		{
			senderObj = getRTCpsObject(caller);
		}
		else
		{
			senderObj = senderOm;
		}
		RTCpsObject targetObj;
		if (targetOm == null)
		{
			targetObj = getRTCpsObject(target);
		}
		else
		{
			targetObj = targetOm;
		}

		RTMessage msg = new RTMessage(senderObj);
		msg.setServer(targetObj); // TODO: verify this value, shouldn't this be
		// "sender" in case of output messages?
		CpsSelector sel = createSelector(target, selector, args, key);
		msg.setSelector(sel);
		msg.setArguments(args);
		Object returnvalue;

		try
		{
			if (senderOm != null)
			{
				msg.setDirection(MessageDirection.OUTGOING);
				returnvalue = senderOm.deliverOutgoingMessage(senderObj, targetObj, msg);
			}
			else if (targetOm != null)
			{
				msg.setDirection(MessageDirection.INCOMING);
				returnvalue = targetOm.deliverIncomingMessage(senderObj, targetObj, msg);
			}
			else
			{
				returnvalue = invokeMessageToInner(msg);
			}
		}
		catch (RuntimeException e)
		{
			if (!RT_DEBUG)
			{
				e.fillInStackTrace();
			}
			throw e;
		}
		return returnvalue;
	}

	/**
	 * Instance method call with a return from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(Object caller, Object target, String selector, Object[] args, String key)
	{
		return handleMethodCall(caller, target, selector, args, key, EnumSet.noneOf(MessageState.class));
	}

	/**
	 * Instance method call without a return from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(Object caller, Object target, String selector, Object[] args, String key)
	{
		handleMethodCall(caller, target, selector, args, key, EnumSet.of(MessageState.VOID_RETURN));
	}

	/**
	 * Instance method call with a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(String staticcaller, Object target, String selector, Object[] args,
			String key)
	{
		return handleMethodCall(null, target, selector, args, key, EnumSet.of(MessageState.STATIC_SENDER));
	}

	/**
	 * Instance method call without a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(String staticcaller, Object target, String selector, Object[] args,
			String key)
	{
		handleMethodCall(null, target, selector, args, key, EnumSet.of(MessageState.STATIC_SENDER,
				MessageState.VOID_RETURN));
	}

	/**
	 * Static method call with a return value from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(Object caller, String target, String selector, Object[] args, String key)
	{
		// no filters possible on a static target
		return Invoker.invoke(target, selector, args);
	}

	/**
	 * Static method call without a return value from an instance context
	 * 
	 * @param caller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(Object caller, String target, String selector, Object[] args, String key)
	{
		// no filters possible on a static target
		Invoker.invoke(target, selector, args);
	}

	/**
	 * Static method call with a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 * @return
	 */
	public static Object handleReturnMethodCall(String staticcaller, String target, String selector, Object[] args,
			String key)
	{
		// no filters possible on a static target
		return Invoker.invoke(target, selector, args);
	}

	/**
	 * Static method call without a return value from a static context
	 * 
	 * @param staticcaller
	 * @param target
	 * @param selector
	 * @param args
	 */
	public static void handleVoidMethodCall(String staticcaller, String target, String selector, Object[] args,
			String key)
	{
		// no filters possible on a static target
		Invoker.invoke(target, selector, args);
	}

	/**
	 * Invoke a RTMessage to the inner object
	 * 
	 * @param msg
	 * @return
	 */
	public static Object invokeMessageToInner(RTMessage msg)
	{
		Object target = null;
		if (msg.getTarget() instanceof RTCpsObject)
		{
			target = msg.getTarget().getObject();
		}
		else
		{
			throw new IllegalStateException("Message target is not an RTCpsObject");
		}
		if (msg.getTarget().isInnerObject() && msg.hasState(MessageState.CONSTRUCTOR))
		{
			logger.info("Dispatch to inner from a constructor, returning null");
			return null;
		}
		MethodInfo methodInfo = null;
		if (msg.getSelector() instanceof CpsSelectorMethodInfo)
		{
			methodInfo = ((CpsSelectorMethodInfo) msg.getSelector()).getMethodInfo();
		}
		return Invoker.invoke(target, msg.getSelector().getName(), msg.getArguments(), methodInfo);
	}

	/**
	 * Create a RTCpsObject for a given object
	 * 
	 * @param sender
	 * @return
	 */
	public static RTCpsObject getRTCpsObject(Object sender)
	{
		if (sender == null)
		{
			return null;
		}
		if (sender instanceof RTCpsObject)
		{
			return (RTCpsObject) sender;
		}
		RTCpsObject result = ObjectManagerHandler.getObjectManager(sender, repository);
		if (result == null)
		{
			Concern crn = repository.get(sender.getClass().getName(), Concern.class);
			if (crn != null)
			{
				result = new SimpleCpsObject(sender, crn.getTypeReference());
			}
			else
			{
				result = new SimpleCpsObject(sender, null);
			}
		}
		return result;
	}

	/**
	 * Create a CpsSelector for the given input
	 * 
	 * @param target
	 * @param name
	 * @param args
	 * @return
	 */
	public static CpsSelector createSelector(Object target, String name, Object[] args, String key)
	{
		if (target == null)
		{
			return new CpsSelectorImpl(name);
		}
		Concern concern = repository.get(target.getClass().getName(), Concern.class);
		if (concern == null)
		{
			return new CpsSelectorImpl(name);
		}
		Type type = concern.getTypeReference().getReference();
		if (type == null)
		{
			return new CpsSelectorImpl(name);
		}
		List<MethodInfo> hits = new ArrayList<MethodInfo>();
		Signature sig = type.getSignature();
		if (sig != null)
		{
			for (MethodInfoWrapper wrap : sig.getMethodInfoWrapper(name))
			{
				if (wrap.getStatus() == MethodStatus.EXISTING && wrap.getRelation() != MethodRelation.REMOVED)
				{
					MethodInfo mi = wrap.getMethodInfo();
					if (mi.getParameters().size() == args.length)
					{
						if (key != null && !key.isEmpty() && mi.getHashKey().equals(key))
						{
							return new CpsSelectorMethodInfo(mi);
						}
						hits.add(mi);
					}
				}
			}
		}
		else
		{
			for (MethodInfo mi : type.getMethods())
			{
				if (mi.getName().equals(name) && mi.getParameters().size() == args.length)
				{
					if (key != null && !key.isEmpty() && mi.getHashKey().equals(key))
					{
						return new CpsSelectorMethodInfo(mi);
					}
					hits.add(mi);
				}
			}
		}

		if (hits.isEmpty())
		{
			return new CpsSelectorImpl(name);
		}
		else if (hits.size() == 1)
		{
			return new CpsSelectorMethodInfo(hits.get(0));
		}
		// TODO find the best by guessing
		// ...

		return new CpsSelectorImpl(name);
	}
}
