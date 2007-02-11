/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.Hashtable;
import java.util.Iterator;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * A FilterType represents a type a filter can have. It is not bound to a
 * specific filter (i.e. a member of the filter), but a property a filter can
 * have. Therefore it is not a concern anymore but a normal RepositoryEntity.
 */
public class FilterType extends RepositoryEntity
{
	private static final long serialVersionUID = 7876700154644254076L;

	public String type;

	private FilterAction acceptCallAction;

	private FilterAction rejectCallAction;

	private FilterAction acceptReturnAction;

	private FilterAction rejectReturnAction;

	// public final static String WAIT = "Wait";
	// public final static String DISPATCH = "Dispatch";
	// public final static String ERROR = "Error";
	// public final static String META = "Meta";
	// public final static String SUBSTITUTION = "Substitution";
	// public final static String CUSTOM = "Custom";
	// public final static String SEND = "Send";
	// public final static String PREPEND = "Prepend";
	// public final static String APPEND = "Append";
	// public final static String BEFORE = "Before";
	// public final static String AFTER = "After";

	/**
	 * Contains a mapping from strings representing filtertypes to FilterType
	 * objects
	 */
	private static Hashtable filterTypeMapping;

	/**
	 * @return java.lang.String
	 * @roseuid 401FAA650206
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param typeValue
	 * @roseuid 401FAA65020F
	 */
	public void setType(String typeValue)
	{
		this.type = typeValue;
	}

	/**
	 * @return the acceptCallAction
	 */
	public FilterAction getAcceptCallAction()
	{
		return acceptCallAction;
	}

	/**
	 * @param acceptCallAction the acceptCallAction to set
	 */
	public void setAcceptCallAction(FilterAction acceptCallAction)
	{
		this.acceptCallAction = acceptCallAction;
	}

	/**
	 * @return the acceptReturnAction
	 */
	public FilterAction getAcceptReturnAction()
	{
		return acceptReturnAction;
	}

	/**
	 * @param acceptReturnAction the acceptReturnAction to set
	 */
	public void setAcceptReturnAction(FilterAction acceptReturnAction)
	{
		this.acceptReturnAction = acceptReturnAction;
	}

	/**
	 * @return the rejectCallAction
	 */
	public FilterAction getRejectCallAction()
	{
		return rejectCallAction;
	}

	/**
	 * @param rejectCallAction the rejectCallAction to set
	 */
	public void setRejectCallAction(FilterAction rejectCallAction)
	{
		this.rejectCallAction = rejectCallAction;
	}

	/**
	 * @return the rejectReturnAction
	 */
	public FilterAction getRejectReturnAction()
	{
		return rejectReturnAction;
	}

	/**
	 * @param rejectReturnAction the rejectReturnAction to set
	 */
	public void setRejectReturnAction(FilterAction rejectReturnAction)
	{
		this.rejectReturnAction = rejectReturnAction;
	}

	public static FilterType getFilterType(String name)
	{
		if (filterTypeMapping == null)
		{
			createFilterTypeMapping();
		}

		return (FilterType) filterTypeMapping.get(name.toLowerCase());
	}

	private static void createFilterTypeMapping()
	{
		DataStore ds = DataStore.instance();
		filterTypeMapping = new Hashtable();
		Iterator filterTypeIter = ds.getAllInstancesOf(FilterType.class);
		while (filterTypeIter.hasNext())
		{
			FilterType filterType = (FilterType) filterTypeIter.next();
			filterTypeMapping.put(filterType.getType().toLowerCase(), filterType);
		}
	}
}
