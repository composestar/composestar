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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.Message;

/**
 * Keeps a record of all the breadcrumbs. By default there is one dispatch graph
 * present in the repository. It's a fully resolved graph using the preselected
 * FilterModuleOrder.
 * 
 * @author Michiel Hendriks
 */
public class DispatchGraph
{
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
	 * Contains a map for each concern. The map contains the breadcrumbs with
	 * the selector (as string) as the key.
	 */
	protected Map crumbs;

	/**
	 * Handle to the resolve class used to resolve the breadcrumbs from the
	 * input data.
	 */
	protected transient Resolver resolver;
	
	public DispatchGraph(int inMode)
	{
		mode = inMode;
		crumbs = new HashMap();
		resolver = new Resolver(this);
	}

	public Resolver getResolver()
	{
		return resolver;
	}

	public boolean getAutoResolve()
	{
		return autoResolve;
	}

	public void setAutoResolve(boolean inval)
	{
		autoResolve = inval;
	}

	public int getMode()
	{
		return mode;
	}

	public void addCrumb(Breadcrumb crumb)
	{
		ConcernCrumbs concernCrumbs = (ConcernCrumbs) crumbs.get(crumb.getConcern());
		if (concernCrumbs == null)
		{
			concernCrumbs = new ConcernCrumbs(crumb.getConcern());
			crumbs.put(crumb.getConcern(), concernCrumbs);
		}
		concernCrumbs.addCrumb(crumb);
	}

	public Iterator getCrumbs(Concern concern)
	{
		// TODO: redo
		throw new NotImplementedException();
	}

	// TODO: redo
	public Breadcrumb getInputCrumb(Concern concern, Message msg) throws ModuleException
	{
		if (concern == null)
		{
			throw new ModuleException("Called getInputCrumb without a valid concern instance", DIGGER.MODULE_NAME);
		}
		if ((msg == null) || (msg.getSelector() == null))
		{
			throw new ModuleException("Called getInputCrumb without a valid message", DIGGER.MODULE_NAME);
		}
		return getInputCrumb(concern, msg.getSelector().getName());
	}

	// TODO: redo
	public Breadcrumb getInputCrumb(Concern concern, String selector) throws ModuleException
	{
		if (concern == null)
		{
			throw new ModuleException("Called getInputCrumb without a valid concern instance", DIGGER.MODULE_NAME);
		}
		Breadcrumb result = null;
		ConcernCrumbs concernCrumbs = (ConcernCrumbs) crumbs.get(concern);
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
		//TODO: this doesn't work yet
		/*
		FireModel fm;
		if (fmOrder == null)
		{
			fm = new FireModel(concern);
		}
		else
		{
			fm = new FireModel(concern, fmOrder);
		}
		ExecutionModel em;
		switch (mode)
		{
			case MODE_BASIC:
				em = fm.getExecutionModel(FireModel.INPUT_FILTERS, selector);
				break;
			default:
				//MethodInfo methodInfo = ((Type) concern.getPlatformRepresentation()).getMethod(selector, types);				
				//em = fm.getExecutionModel(FireModel.INPUT_FILTERS, methodInfo, FireModel.STRICT_SIGNATURE_CHECK);
				break;
		}
		*/
		throw new NotImplementedException();
	}

	public Iterator getAllCrumbs()
	{
		// TODO: redo
		throw new NotImplementedException();
	}
}
