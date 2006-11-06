package Composestar.RuntimeCore.FLIRT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchToInnerAction;
import Composestar.RuntimeCore.FLIRT.Exception.ComposestarRuntimeException;
import Composestar.RuntimeCore.FLIRT.Exception.ExternalNotFoundException;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterModuleRuntime;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Policy.FilterPolicy;
import Composestar.RuntimeCore.FLIRT.Policy.PolicyExecutionResult;
import Composestar.RuntimeCore.Utils.ChildRunnable;
import Composestar.RuntimeCore.Utils.ChildThread;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;
import Composestar.RuntimeCore.Utils.SyncBuffer;
import Composestar.RuntimeCore.Utils.ThreadPool;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * ObjectManager.java 2170 2006-10-20 15:34:12Z elmuerte $ This class manages
 * the filtering process for each object. The an object's objectManager is
 * obtained by with the static method getObjectManagerFor. Subsequent calls to
 * the method return the original object manager; this means that it exists a
 * single object manager for each object. It is the responsibility of the
 * objectManager to receive intercepted messages from the
 * MessageInterceptionLayer. And to start the filtering process. The Object
 * manager is NOT thread safe. Finally, the object manager also manages the
 * filtermodules that are imposed on the object that manages.
 */
public class ObjectManager implements ChildRunnable
{

	/**
	 * The Object that is being managed
	 */
	public Object theObject;

	private ArrayList filterModules;

	private ArrayList methods;

	private ArrayList conditions;

	private SyncBuffer messageQueue;

	private boolean working = false;

	private static final Object[] EmptyObjectArray = {};

	/**
	 * @roseuid 41161A8D008E
	 */
	public synchronized void run()
	{
		this.working = true;

		do
		{
			Message msg = (Message) this.messageQueue.consume();
			Object reply = null;
			try
			{
				reply = this.receiveMessage(msg);
			}
			catch (Exception e)
			{
				Debug.out(Debug.MODE_ERROR, "FLIRT", "An exception was thrown from within a filter.");
				Debug.out(Debug.MODE_ERROR, "FLIRT", "Message was `" + msg.getSelector() + "' for target `"
						+ msg.getTarget() + "' from sender `" + msg.getSender() + "'.");
				Debug.out(Debug.MODE_ERROR, "FLIRT", "Internal Compose* stack trace:");
				e.printStackTrace();
				// This method is executed in a seperate thread and will die on
				// exceptions.
			}
			finally
			{
				msg.setResponse(reply);
			}
		} while (!this.messageQueue.isEmpty()); // dowhile because of concurency
		this.working = false;
	}

	public void notifyMessageConsumer()
	{
		if (!this.working)
		{
			ChildThread child = ThreadPool.getChildThread(this);
			child.start();
		}
	}

	/**
	 * Constructs a new Object Manager - made public for testing purposes.
	 * 
	 * @param o
	 * @roseuid 3F36584D0116
	 * @param store
	 */
	public ObjectManager(Object o, DataStore store)
	{
		this.messageQueue = new SyncBuffer();
		// DataStore store1 = store;
		filterModules = new ArrayList();
		methods = new ArrayList(); // list with methodBinding objects
		conditions = new ArrayList(); // list with conditionBinding objects

		// find the concern
		theObject = o;
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "ObjectManager created for object of type '"
					+ o.getClass().getName() + "'.");
		}
		Concern concern = (Concern) store.getObjectByID(o.getClass().getName());
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Checking concern '" + concern + "'...");
		}

		if (concern != null)
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Found concern for '" + concern.getQualifiedName() + "'.");
			}

			// Try to fetch the order for this concern from the repository
			if (concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null
					&& concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) instanceof FilterModuleOrder)
			{
				// For each order add the filtermodule to this object manager
				FilterModuleOrder fmo = (FilterModuleOrder) concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
				Vector order = fmo._order;
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Getting " + order.size() + " orderings.");
				}
				for (int i = 0; i < order.size(); i++)
				{
					// The linking does not work with deserializing therefore we
					// need to get it from the map.
					String filtermodulename = (String) order.elementAt(i);
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Looking up filtermodule '" + filtermodulename
								+ "'.");
						Debug.out(Debug.MODE_DEBUG, "FLIRT", "FilterModule map: "
								+ RepositoryLinker.filterModuleReferenceMap);
					}
					FilterModuleRuntime rootfmruntime = (FilterModuleRuntime) RepositoryLinker.filterModuleReferenceMap
							.get(filtermodulename);

					// Just in case :)
					if (rootfmruntime != null)
					{
						// Make the parent link to the me :)

						rootfmruntime.parentname = concern.getName();

						// Create a copy of the filtermodule!
						FilterModuleRuntime fmruntime;
						try
						{
							fmruntime = (FilterModuleRuntime) rootfmruntime.clone();
						}
						catch (CloneNotSupportedException e)
						{
							e.printStackTrace();
							fmruntime = rootfmruntime;
						}
						if (Debug.SHOULD_DEBUG)
						{
							Debug.out(Debug.MODE_DEBUG, "FLIRT", "Comparing sizes of externals after cloning: "
									+ rootfmruntime.getExternals().size() + " = " + fmruntime.getExternals().size());
						}

						fmruntime.setObjectManager(this);

						if (Debug.SHOULD_DEBUG)
						{
							Debug
									.out(Debug.MODE_INFORMATION, "FLIRT", "Found FilterModuleRuntime '" + fmruntime
											+ "'.");
						}
						// Add the filtermodule to this objectmanager
						this.addFilterModule(fmruntime);

						// Resolve and add the internals of this filtermodule
						FilterModule filtermodule = (FilterModule) fmruntime.getReference();

						// Add the internals
						this.addInternals(concern, fmruntime, filtermodule);

						// And of course the externals
						this.addExternals(concern, fmruntime, filtermodule);

						// the conditions
						this.addConditions(concern, fmruntime, filtermodule);

						// the condition references, done via superimposition
						this.addConditionReferences(concern, fmruntime, filtermodule);

						// the methods
						this.addMethods(concern, fmruntime, filtermodule);

						// the method references, done via superimposition
						this.addMethodReferences(concern, fmruntime, filtermodule);
					}
					else
					{
						// Is this error or information?
						if (Debug.SHOULD_DEBUG)
						{
							Debug.out(Debug.MODE_ERROR, "FLIRT", "No FilterModuleRuntime found for '"
									+ filtermodulename + "'.");
						}
					}
				}
			}
		}
		else
		{
			// Oops this is not a concern we do not need to filter this!
		}
	}

	private void addInternals(Concern concern, FilterModuleRuntime fmruntime, FilterModule filtermodule)
	{
		// Add the internals
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Looking for internals of filtermodule '" + filtermodule
					+ "'...");
		}
		Iterator internalIterator = filtermodule.getInternalIterator();
		while (internalIterator.hasNext())
		{
			Internal internal = (Internal) internalIterator.next();
			String internalname = internal.getName();
			String internaltype = internal.getType().getQualifiedName();
			Object internalobject;
			// try
			{
				internalobject = Invoker.getInstance().requestInstance(internaltype, EmptyObjectArray);
			}
			/*
			 * catch(Exception exp) { throw new
			 * InternalNotFoundException(internaltype,internalname,concern.getName()); }
			 */
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound internal '" + internalname + "' of type '"
						+ internaltype + "' inside object '" + internalobject + "'.");
			}
			fmruntime.addInternal(internalname, internalobject);
		}
	}

	private void addExternals(Concern concern, FilterModuleRuntime fmruntime, FilterModule filtermodule)
	{
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Looking for externals of filtermodule '" + filtermodule
					+ "'...");
		}

		Iterator externalIterator = filtermodule.getExternalIterator();
		while (externalIterator.hasNext())
		{
			External external = (External) externalIterator.next();
			String externalname = external.getName();
			String externaltype = external.getType().getQualifiedName();
			Object externalobject;

			if (external.shortinit != null)
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound short external '"
							+ external.shortinit.getName() + "' of type '" + external.shortinit.getQualifiedName()
							+ "'.");
				}
				String extarget = external.getShortinit().getInitTarget();
				String exselector = external.getShortinit().getInitSelector();
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound external '" + extarget + "' of type '"
							+ externaltype + "'.");
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tGet it from '" + extarget + '.' + exselector + "'.");
				}
				try
				{
					externalobject = Invoker.getInstance().invoke(extarget, exselector, EmptyObjectArray);
				}
				catch (Exception exp)
				{
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_ERROR, "FLIRT", "Failed to invoke '" + extarget + '.' + exselector + "'.");
					}
					throw new ExternalNotFoundException(externaltype, externalname, concern.getName());
				}
				if (externalobject != null)
				{
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tLocated external '"
								+ externalobject.getClass().getName() + "'.");
					}
					fmruntime.addExternal(externalname, externalobject);
				}

			}
			else if (external.longinit != null)
			{
				// TODO resolve the long external jump
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound long external '" + external.longinit.getName()
							+ "' of type '" + external.longinit.getQualifiedName() + "'.");
				}
				throw new ComposestarRuntimeException("The use of external references is not supported.");
			}
			else
			{
				// fixme Is this mode error?
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_ERROR, "FLIRT", "\tOops bad external!!");
				}
			}
		}
	}

	private void addConditions(Concern concern, FilterModuleRuntime fmruntime, FilterModule filtermodule)
	{
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Looking for conditions of filtermodule '" + filtermodule
					+ "'...");
		}
		Iterator conditionIterator = filtermodule.getConditionIterator();
		while (conditionIterator.hasNext())
		{
			Condition condition = (Condition) conditionIterator.next();
			String conditionname = condition.getName();

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound condition '" + conditionname + "'.");
			}

			if (condition.getShortref() != null)
			{ // Condition to internal/external or static call
				Reference cr = condition.getShortref();
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound short conditionref '" + cr.getName()
							+ "' in package '" + cr.getPackage() + "'.");
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tQualified name is '" + cr.getQualifiedName() + "'.");
				}
				String ctarget = cr.getQualifiedName();
				String cselector = (String) condition.getDynObject("selector");
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound target '" + ctarget + "' and selector '"
							+ cselector + "'.");
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tAdding condition[" + conditionname + "]: " + ctarget
							+ " = " + cselector);
				}
				fmruntime.addCondition(conditionname, ctarget + '=' + cselector);
			}
			else if (condition.getLongref() != null)
			{ // Condition to other filtermodule
				// TODO: Resolve the references to the referred conditions in
				// other concerns/filtermodules
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_ERROR, "FLIRT", "Found long conditionref '" + condition.getLongref() + "'.");
				}
				throw new ComposestarRuntimeException("The use of condition references is not supported.");
			}
		}
	}

	private void addConditionReferences(Concern concern, FilterModuleRuntime fmruntime, FilterModule filtermodule)
	{

	}

	private void addMethodReferences(Concern concern, FilterModuleRuntime fmruntime, FilterModule filtermodule)
	{

	}

	private void addMethods(Concern concern, FilterModuleRuntime fmruntime, FilterModule filtermodule)
	{

	}

	/**
	 * Retrieves the Object Manager associated with the object o. If there is
	 * none, a new one is created.
	 * 
	 * @param o The object whose manager is needed.
	 * @return the manager of the object o
	 * @roseuid 3F36584D0117
	 * @param store
	 */
	public static ObjectManager getObjectManagerFor(Object o, DataStore store)
	{
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Getting object manager for object of type '"
					+ o.getClass().getName() + "'.");
		}

		ObjectManager obj = (ObjectManager) GlobalObjectManager.getObjectManagerFor(o);
		if (obj == null)
		{
			// No object manager present, so create one
			obj = new ObjectManager(o, store);
			GlobalObjectManager.setObjectManagerFor(o, obj);
		}
		return obj;
	}

	public static boolean hasFilterModules(Object o, DataStore store)
	{
		Concern concern = (Concern) store.getObjectByID(o.getClass().getName());
		return (concern != null && concern.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY) != null);
	}

	/**
	 * Delivers a message to the object that is being managed. This is the point
	 * of contact with the Message Interception Layer. The message is filtered
	 * and a return value is given.
	 * 
	 * @param receiver of the message
	 * @param msg the message
	 * @param sender
	 * @return the return value of the message, if the method's return type is
	 *         void return null.
	 * @roseuid 3F36584D0121
	 */
	public Object deliverIncomingMessage(Object sender, Object receiver, Message msg)
	{
		if (Debug.SHOULD_DEBUG)
		{
			if (sender != null)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "INCOMING message '" + msg.getSelector() + "' from caller '"
						+ sender.getClass() + "' for target '" + receiver.getClass() + "'.");
			}
			else
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "INCOMING message '" + msg.getSelector() + "' for target '"
						+ receiver.getClass() + "'.");
			}
		}

		// Message m = new Message(msg);
		msg.setInner(receiver);
		msg.setTarget(receiver);

		// add the message to the message queue
		this.messageQueue.produce(msg);

		// notify objectmanager that messagequeue is not empty
		this.notifyMessageConsumer();

		// read the returnvalue from the message's response buffer
		return msg.getResponse();
	}

	/**
	 * Delivers a message to the object that is being managed. This is the point
	 * of contact with the Message Interception Layer. The message is filtered
	 * and a return value is given.
	 * 
	 * @param receiver of the message
	 * @param msg the message
	 * @param sender
	 * @return the return value of the message, if the method's return type is
	 *         void return null.
	 * @roseuid 3F36584D0121
	 */
	public Object deliverOutgoingMessage(Object sender, Object receiver, Message msg)
	{
		if (Debug.SHOULD_DEBUG)
		{
			if (sender != null)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "OUTGOING message '" + msg.getSelector() + "' from caller '"
						+ sender.getClass() + "' for target '" + receiver.getClass() + "'.");
			}
			else
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "OUTGOING message '" + msg.getSelector() + "' for target '"
						+ receiver.getClass() + "'.");
			}
		}

		Message m = new Message(msg);
		m.setInner(sender);
		m.setTarget(receiver);

		// add the message to the objectmanager's message queue
		this.messageQueue.produce(m);

		// notify objectmanager that queue is not empty
		this.notifyMessageConsumer();

		// read the response from the responsebuffer...
		Object reply = m.getResponse();

		if (reply instanceof MessageList)
		{
			reply = ((MessageList) reply).getMessageAfterOutputFilter();
		}

		if (reply instanceof Message)
		{
			((Message) reply).setDirection(Message.INCOMING);
		}

		return reply;
	}

	/**
	 * Receives a message and filters it, returning the appropriate value This
	 * method passes the received message through each of the filter modules
	 * attached to the managed object. The way the message is treated by each
	 * filter module is given by a policy
	 * 
	 * @return the return value of the message, if the method's return type is
	 *         void return null
	 * @roseuid 3F36584D012B
	 * @param aSingleMessage
	 */
	public Object receiveMessage(Message aSingleMessage)
	{
		// Set used vars
		boolean wasAccepted = false;
		ArrayList filterList;

		MessageList aMessage = new MessageList(aSingleMessage);
		// try
		// {
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Number of filtermodules is " + filterModules.size() + '.');
		}
		// Test each filter, in the filterModules list.
		for (int i = 0; i < filterModules.size(); i++)
		{
			// Get the filtermodule
			FilterModuleRuntime fm = (FilterModuleRuntime) filterModules.get(i);

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_DEBUG, "FLIRT", "Message is "
						+ (aMessage.getDirection() == Message.INCOMING ? "INCOMING" : "OUTGOING") + '.');
			}

			filterList = (aMessage.getDirection() == Message.INCOMING ? fm.getInputFilters() : fm.getOutputFilters());

			// michielh: Why this code? if the first FM doesn't have
			// outputfilters and the other do it won't work
			// if(filterList.isEmpty() && aMessage.getDirection() ==
			// Message.OUTGOING )
			// {
			// if(Debug.SHOULD_DEBUG)
			// Debug.out(Debug.MODE_INFORMATION,"FLIRT","No outputfilters,
			// returning message.");
			// return aMessage;
			// }

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tNumber of filters is " + filterList.size() + '.');
			}

			// Get the policy, for now this returns a MetaPolicy!
			FilterPolicy fp = FilterPolicy.getPolicy();

			// Execute the policy, this really starts the interpreter for the
			// input filters
			PolicyExecutionResult per = fp.executeFilterPolicy(fm, filterList, aMessage);

			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tContinue to next filtermodule? " + per.shouldContinue());
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tIs the message accepted? " + per.wasAccepted());
			}
			if (!per.shouldContinue())
			{
				return per.getActionResult();
			}
			wasAccepted = wasAccepted || per.wasAccepted();
		}

		if (aMessage.getDirection() == Message.OUTGOING)
		{
			return aMessage;
		}

		if (filterModules.isEmpty())
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "No filtermodules, returning DefaultDispatchToInnerAction");
			}
			// return Invoker.getInstance().invoke(aMessage.getTarget(),
			// aMessage.getSelector(), aMessage.getArguments());
			return new DispatchToInnerAction(aMessage, true, aMessage.getFirstMessage().getTarget(), aMessage
					.getFirstMessage().getSelector(), aMessage.getArguments());
		}
		// throw new MessageNotFilteredException("The message:
		// "+aMessage.getSelector()+ " for target:
		// "+aMessage.getInner().getClass().getName()+" was not filtered!");
		// }
		/*
		 * catch (Exception e) { Debug.out(Debug.MODE_ERROR,"FLIRT","Encountered
		 * error while interpreting: "+e.getMessage()); e.printStackTrace(); }
		 */

		return null;
	}

	/**
	 * Adds a new filter module on top of the existing ones
	 * 
	 * @param newModule the new filter module
	 * @roseuid 3F36584D013E
	 */
	public void addFilterModule(FilterModuleRuntime newModule)
	{
		this.filterModules.add(newModule);
	}

	/**
	 * Returns a listing of the filter modules in the order in which messages
	 * are passed
	 * 
	 * @return a list containing the modules
	 * @roseuid 3F36584D0140
	 */
	public ArrayList getFilterModules()
	{
		return this.filterModules;
	}

	/**
	 * Retrives a Filtermodule by its name.
	 * 
	 * @param moduleName the name of the desired filtermodule
	 * @return the filter module if it is imposed on the managed object, null
	 *         otherwise
	 * @roseuid 3F36584D0148
	 */
	public FilterModuleRuntime getFilterModule(String moduleName)
	{
		for (int i = 0; i < filterModules.size(); i++)
		{
			FilterModuleRuntime f = (FilterModuleRuntime) filterModules.get(i);
			if (((FilterModule) (f.getReference())).getName().equals(moduleName))
			{
				return f;
			}
		}
		return null;
	}

	/**
	 * Removes a filter module given its name
	 * 
	 * @param moduleName the name of the module
	 * @roseuid 3F36584D0153
	 */
	public void removeFilterModule(String moduleName)
	{
		this.removeFilterModule(this.getFilterModule(moduleName));
	}

	/**
	 * Removes a module
	 * 
	 * @param mod module to remove
	 * @roseuid 3F36584D0155
	 */
	public void removeFilterModule(FilterModuleRuntime mod)
	{
		this.filterModules.remove(mod);
	}

	/**
	 * Resets the bindings of the objects - managers This method is provieded
	 * for testing purposes
	 * 
	 * @roseuid 3F36584D015D
	 */
	public static void reset()
	{
		GlobalObjectManager.reset();
	}

	/**
	 * @return ArrayList
	 * @roseuid 411B20160291
	 */
	public ArrayList getMethodBindings()
	{
		return this.methods;
	}

	/**
	 * @return ArrayList
	 * @roseuid 411B20200318
	 */
	public ArrayList getConditionBindings()
	{
		return this.conditions;
	}
}
