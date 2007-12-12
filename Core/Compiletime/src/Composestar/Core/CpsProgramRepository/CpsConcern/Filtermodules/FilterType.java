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

	// public static final String WAIT = "Wait";
	// public static final String DISPATCH = "Dispatch";
	// public static final String ERROR = "Error";
	// public static final String META = "Meta";
	// public static final String SUBSTITUTION = "Substitution";
	// public static final String CUSTOM = "Custom";
	// public static final String SEND = "Send";
	// public static final String PREPEND = "Prepend";
	// public static final String APPEND = "Append";
	// public static final String BEFORE = "Before";
	// public static final String AFTER = "After";

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
	 * @param value the acceptCallAction to set
	 */
	public void setAcceptCallAction(FilterAction value)
	{
		acceptCallAction = value;
	}

	/**
	 * @return the acceptReturnAction
	 */
	public FilterAction getAcceptReturnAction()
	{
		return acceptReturnAction;
	}

	/**
	 * @param value the acceptReturnAction to set
	 */
	public void setAcceptReturnAction(FilterAction value)
	{
		acceptReturnAction = value;
	}

	/**
	 * @return the rejectCallAction
	 */
	public FilterAction getRejectCallAction()
	{
		return rejectCallAction;
	}

	/**
	 * @param value the rejectCallAction to set
	 */
	public void setRejectCallAction(FilterAction value)
	{
		rejectCallAction = value;
	}

	/**
	 * @return the rejectReturnAction
	 */
	public FilterAction getRejectReturnAction()
	{
		return rejectReturnAction;
	}

	/**
	 * @param value the rejectReturnAction to set
	 */
	public void setRejectReturnAction(FilterAction value)
	{
		rejectReturnAction = value;
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
