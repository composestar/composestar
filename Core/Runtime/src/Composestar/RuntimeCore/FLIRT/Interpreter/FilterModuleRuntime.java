package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import Composestar.RuntimeCore.FLIRT.ObjectManager;
import Composestar.RuntimeCore.FLIRT.Exception.InvalidConditionException;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Reflection.MessageInfo;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterModuleRuntime.java 2072 2006-10-15 12:44:56Z elmuerte $
 */
public class FilterModuleRuntime extends ReferenceEntityRuntime implements Interpretable, ConditionResolver, Cloneable
{
	private ArrayList methods;

	private HashMap conditions;

	private Dictionary internals;

	private Dictionary externals;

	private String name;

	private ArrayList outputfilters = null;

	private ArrayList inputfilters = null;

	private ObjectManager parent;

	public String parentname = null;

	private static final Object[] EmptyObjectList = {};

	/**
	 * @roseuid 40DD8B1901B3
	 */
	public FilterModuleRuntime()
	{
		this.inputfilters = new ArrayList();
		this.outputfilters = new ArrayList();
		this.externals = new Hashtable();
		this.internals = new Hashtable();
		this.methods = new ArrayList();
		this.conditions = new HashMap();
	}

	/**
	 * @param inputfilters
	 * @param outputfilters
	 * @roseuid 40DD57450241
	 */
	public FilterModuleRuntime(ArrayList inputfilters, ArrayList outputfilters)
	{
		this.inputfilters = inputfilters;
		this.outputfilters = outputfilters;
		this.externals = new Hashtable();
		this.internals = new Hashtable();
		this.methods = new ArrayList();
		this.conditions = new HashMap();
	}

	public FilterModuleRuntime(FilterModuleRuntime fmr)
	{
		this.name = fmr.name;
		this.parent = fmr.parent;
		this.parentname = fmr.parentname;
		this.inputfilters = fmr.inputfilters;
		this.outputfilters = fmr.outputfilters;
		this.methods = fmr.methods;
		this.conditions = fmr.conditions;
		this.externals = fmr.externals;
		this.internals = new Hashtable();

		this.reference = fmr.reference;
	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD62410168
	 */
	public boolean interpret(MessageList m, Dictionary context)
	{
		return true;
	}

	/**
	 * @param name
	 * @roseuid 4116A59D008B
	 */
	public void init(String name)
	{
		this.name = name;
		this.externals = new Hashtable();
		this.internals = new Hashtable();
		this.inputfilters = new ArrayList();
		this.outputfilters = new ArrayList();
		this.methods = new ArrayList();
		this.conditions = new HashMap();
	}

	/**
	 * @param fmr
	 * @roseuid 4116A59D0095
	 */
	public void init(FilterModuleRuntime fmr)
	{
		this.name = fmr.getName();
		this.externals = fmr.getExternals();
		this.internals = fmr.getInternals();
		this.inputfilters = fmr.getInputFilters();
		this.outputfilters = fmr.getOutputFilters();
		this.methods = fmr.getMethods();
		this.conditions = fmr.getConditions();
	}

	/**
	 * @return Composestar.Runtime.util.Dictionary
	 * @roseuid 4116A59D009F
	 */
	public Dictionary getInternals()
	{
		return this.internals;
	}

	/**
	 * @return Composestar.Runtime.util.Dictionary
	 * @roseuid 4116A59D00A9
	 */
	public Dictionary getExternals()
	{
		return this.externals;
	}

	public Object getExternal(String name)
	{
		return this.externals.get(name);
	}

	public Object getInternal(String name)
	{
		return this.internals.get(name);
	}

	/**
	 * @return java.lang.String
	 * @roseuid 4116A59D00AA
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return java.util.ArrayList
	 * @roseuid 4116A59D00B3
	 */
	public ArrayList getInputFilters()
	{
		return this.inputfilters;
	}

	/**
	 * @return java.util.ArrayList
	 * @roseuid 4116A59D00BD
	 */
	public ArrayList getOutputFilters()
	{
		return this.outputfilters;
	}

	/**
	 * @param selector
	 * @roseuid 4116A59D00BE
	 */
	public void addMethod(String selector)
	{
		this.methods.add(selector);
	}

	/**
	 * @param condition
	 * @roseuid 4116A59D00C7
	 * @param reference
	 */
	public void addCondition(String condition, String reference)
	{
		this.conditions.put(condition, reference);
	}

	/**
	 * @param name
	 * @param object
	 * @roseuid 4116A59D00D1
	 */
	public void addExternal(String name, Object object)
	{
		this.externals.put(name, object);
	}

	/**
	 * @param name
	 * @param object
	 * @roseuid 4116A59D00D2
	 */
	public void addInternal(String name, Object object)
	{
		this.internals.put(name, object);
	}

	/**
	 * @param message
	 * @return boolean
	 * @roseuid 4116A59D00EF
	 */
	public boolean shouldNotFilter(MessageList message)
	{
		// check the methods

		/*
		 * useless? String selector = message.getSelector(); int i; ArrayList
		 * temp; ConditionBinding temp2; MethodBinding temp3; String co;
		 */
		return false;
	}

	/**
	 * @param message
	 * @return java.lang.Object
	 * @roseuid 4116A59D00F0
	 */
	/*
	 * seems not to be in use public Object selfDispatch(MessageList message) {
	 * try { // TODO WM: iterate over messages //fixme return
	 * Invoker.getInstance().invoke(this, message.getSelector(),
	 * message.getArguments()); } catch (Exception e) { FilterModuleException
	 * fme = new FilterModuleException("error while dispatching a message " +
	 * message.toShortString()); throw fme; } }
	 */

	/**
	 * @param filter
	 * @roseuid 4116A59D00F9
	 */
	public void addInputFilter(FilterRuntime filter)
	{
		this.inputfilters.add(filter);
	}

	/**
	 * @param filter
	 */
	public void addOutputFilter(FilterRuntime filter)
	{
		this.outputfilters.add(filter);
	}

	/**
	 * @param name
	 * @roseuid 4116A5C3002B
	 */
	public void removeInputFilter(String name)
	{
		this.inputfilters.remove(this.inputfilters.indexOf(name));
	}

	/**
	 * @param name
	 * @return boolean
	 * @roseuid 4116A5CF01CD
	 */
	public boolean resolve(String name)
	{
		try
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tLooking for link '" + name + "'...");
			}
			String cname = this.getCondition(name);
			Object returnvalue;
			if (cname != null)
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tFound link '" + cname + "'.");
				}
				String ctarget = cname.substring(0, cname.lastIndexOf('='));
				String cselector = cname.substring(cname.lastIndexOf('=') + 1);

				Object[] args = EmptyObjectList;

				// Check to see if it is condition on inner!
				if (ctarget.equals("inner"))
				{
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tCondition is inner call: "
								+ MessageInfo.getInner() + " == " + cselector);
					}
					returnvalue = Invoker.getInstance().invoke(MessageInfo.getInner(), cselector, args);
				}
				else if (this.getInternal(ctarget.substring(ctarget.lastIndexOf(':') + 1)) != null)
				{ // It was an internal
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tCondition is internal call: "
								+ this.getInternal(ctarget.substring(ctarget.lastIndexOf(':') + 1)));
					}
					returnvalue = Invoker.getInstance().invoke(
							this.getInternal(ctarget.substring(ctarget.lastIndexOf(':') + 1)), cselector, args);
				}
				else if (this.getExternal(ctarget.substring(ctarget.lastIndexOf(':') + 1)) != null)
				{ // It was an external
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tCondition is external call: "
								+ this.getExternal(ctarget.substring(ctarget.lastIndexOf(':') + 1)));
					}
					returnvalue = Invoker.getInstance().invoke(
							this.getExternal(ctarget.substring(ctarget.lastIndexOf(':') + 1)), cselector, args);
				}
				else
				{ // It was a static call
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tCondition is static call: " + ctarget + '.'
								+ cselector);
					}
					returnvalue = Invoker.getInstance().invoke(ctarget, cselector, args);
				}
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_DEBUG, "FLIRT", "\t\tCondition returned '" + returnvalue.toString() + "'.");
				}
				if (returnvalue.toString().equalsIgnoreCase("true"))
				{
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tCondition is true.");
					}
					return true;
				}
			}
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\tCondition is false.");
			}
			return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			InvalidConditionException ex = new InvalidConditionException("Invalid condition invocation");
			ex.setConditionResolver(this);
			throw ex;
		}
	}

	/**
	 * @param manager
	 * @roseuid 4116A5D10298
	 */
	public void setObjectManager(ObjectManager manager)
	{
		this.parent = manager;
	}

	/**
	 * @return Composestar.Runtime.FLIRT.ObjectManager
	 * @roseuid 4116A5D7011A
	 */
	public ObjectManager getObjectManager()
	{
		return this.parent;
	}

	/**
	 * @return ArrayList
	 * @roseuid 4117660E0051
	 */
	public ArrayList getMethods()
	{
		return this.methods;
	}

	/**
	 * @return ArrayList
	 * @roseuid 4117661B000A
	 */
	public HashMap getConditions()
	{
		return this.conditions;
	}

	public String getCondition(String name)
	{
		return (String) this.conditions.get(name);
	}

	public Object clone() throws CloneNotSupportedException
	{
		return new FilterModuleRuntime(this);
	}

	public ArrayList getDebuggableFilterModules(boolean directionInput)
	{
		return directionInput ? getInputFilters() : getOutputFilters();
	}
}
