/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Keeps a record of all the breadcrumbs. By default there is one dispatch graph
 * present in the repository. It's a fully resolved graph using the preselected
 * FilterModuleOrder.
 * 
 * @author Michiel Hendriks
 */
public class DispatchGraph
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(DIGGER.MODULE_NAME);

	public static final String REPOSITORY_KEY = "DispatchGraph";

	/**
	 * No signature evaluation
	 */
	protected static final int MODE_BASIC = 0;

	/**
	 * Will full signature evaluation
	 */
	protected static final int MODE_FULL = 1;

	/**
	 * The mode the graph was created in
	 */
	protected int mode;

	/**
	 * If true automatically resolve the breadcrumbs added to the graph.
	 */
	protected boolean autoResolve;

	/**
	 * Contains the collection of the breadcrumbs that make up this graph.
	 */
	protected Map<Concern, ConcernCrumbs> crumbs;

	/**
	 * Handle to the resolve class used to resolve the breadcrumbs from the
	 * input data.
	 */
	protected/* transient */Resolver resolver;

	public DispatchGraph(int inMode)
	{
		mode = inMode;
		crumbs = new HashMap<Concern, ConcernCrumbs>();
		resolver = new Resolver(this);
	}

	public Resolver getResolver()
	{
		return resolver;
	}

	/**
	 * @see DispatchGraph#autoResolve
	 */
	public boolean getAutoResolve()
	{
		return autoResolve;
	}

	/**
	 * @see DispatchGraph#autoResolve
	 */
	public void setAutoResolve(boolean inval)
	{
		autoResolve = inval;
	}

	/**
	 * @see DispatchGraph#mode
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * Add a new breadcrumb to the graph. This will automatically construct the
	 * ConcernCrumb instance when it's not realdy present
	 * 
	 * @param crumb
	 */
	public void addCrumb(Breadcrumb crumb)
	{
		ConcernCrumbs concernCrumbs = crumbs.get(crumb.getConcern());
		if (concernCrumbs == null)
		{
			concernCrumbs = new ConcernCrumbs(crumb.getConcern());
			crumbs.put(crumb.getConcern(), concernCrumbs);
		}
		concernCrumbs.addCrumb(crumb);
	}

	/**
	 * @see DispatchGraph#getInputCrumb(Concern, String)
	 */
	public Breadcrumb getInputCrumb(Concern concern, MessageSelector selector) throws ModuleException
	{
		if (concern == null)
		{
			throw new ModuleException("Called getInputCrumb without a valid concern instance", DIGGER.MODULE_NAME);
		}
		if (selector == null)
		{
			throw new ModuleException("Called getInputCrumb without a valid selector", DIGGER.MODULE_NAME);
		}
		return getInputCrumb(concern, selector.getName());
	}

	/**
	 * Returns the input breadcrumb for a given concern and selector. The result
	 * can be null in case there is not crumb for the given selector and one
	 * could not be resolved.
	 * 
	 * @param concern
	 * @param selector
	 * @return
	 * @throws ModuleException
	 */
	public Breadcrumb getInputCrumb(Concern concern, String selector) throws ModuleException
	{
		if (concern == null)
		{
			throw new ModuleException("Called getInputCrumb without a valid concern instance", DIGGER.MODULE_NAME);
		}
		Breadcrumb result = null;
		ConcernCrumbs concernCrumbs = crumbs.get(concern);
		if (concernCrumbs != null)
		{
			result = concernCrumbs.getInputCrumb(selector);
		}
		if (autoResolve && ((result == null) || !result.isResolved()))
		{
			result = performAutoResolve(concern, selector);
		}
		return result;
	}

	protected Breadcrumb performAutoResolve(Concern concern, String selector)
	{
		// TODO: this doesn't work yet
		/*
		 * FireModel fm; if (fmOrder == null) { fm = new FireModel(concern); }
		 * else { fm = new FireModel(concern, fmOrder); } ExecutionModel em;
		 * switch (mode) { case MODE_BASIC: em =
		 * fm.getExecutionModel(FireModel.INPUT_FILTERS, selector); break;
		 * default: //MethodInfo methodInfo = ((Type)
		 * concern.getPlatformRepresentation()).getMethod(selector, types); //em =
		 * fm.getExecutionModel(FireModel.INPUT_FILTERS, methodInfo,
		 * FireModel.STRICT_SIGNATURE_CHECK); break; }
		 */
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a list of resulting messages for a breadcrumb
	 * 
	 * @param crumb
	 * @return list with MessageResult instances in the order of appearance
	 * @throws RecursiveFilterException
	 */
	public List<MessageResult> getResultingMessages(Breadcrumb crumb) throws RecursiveFilterException
	{
		return getResultingMessages(crumb, new ArrayList<Trail>(), crumb.getMessage().getSelector());
	}

	/**
	 * The actual implementation of the the result searching algorithm
	 * 
	 * @param crumb the current crumb being processed
	 * @param stack stack of trails followed, used for recusion checking
	 * @param initialSelector the initial selector, will be used in the
	 *            MessageResult when needed
	 * @return list with MessageResult instances in the order of appearance
	 * @throws RecursiveFilterException
	 */
	protected List<MessageResult> getResultingMessages(Breadcrumb crumb, List<Trail> stack,
			String initialSelector) throws RecursiveFilterException
	{
		List<MessageResult> results = new ArrayList<MessageResult>();
		boolean freshCrumb = true;
		while ((crumb != null) && freshCrumb)
		{
			freshCrumb = false;
			Iterator<Trail> it = crumb.getTrails();
			int cnt = 0;
			while (it.hasNext())
			{
				Trail trail = it.next();
				cnt++;
				if (trail.isEOL())
				{
					// the end of a trail, add it to the results
					results.add(new MessageResult(crumb, trail, initialSelector));
				}
				else
				{
					int idx = stack.indexOf(trail);
					if (idx > -1)
					{
						// trail was already encountered, the result is
						// recursive
						throw new RecursiveFilterException(crumb, stack.subList(idx, stack.size()));
					}
					Breadcrumb nextCrumb = trail.getDestinationCrumb();
					if (cnt == 1)
					{
						// don't recursively call this function for the first
						// trail
						stack.add(trail);
						crumb = nextCrumb;
						freshCrumb = true;
					}
					else
					{
						List<Trail> newStack = new ArrayList<Trail>(stack);
						newStack.add(trail);
						results.addAll(getResultingMessages(nextCrumb, newStack, initialSelector));
					}
				}
			}
		}
		return results;
	}
}
