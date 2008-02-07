/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.CpsProgramRepository.Filters;

import Composestar.Core.COPPER2.FilterTypeMapping;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Factory for the default filters types and filter actions.
 * 
 * @author Michiel Hendriks
 */
public class DefaultFilterFactory
{
	public static final String RESOURCE_KEY = "Repository.DefaultFilterFactory";

	/**
	 * All legacy filter types
	 */
	public static final String[] LEGACY = { FilterTypeNames.DISPATCH, FilterTypeNames.SEND, FilterTypeNames.ERROR,
			FilterTypeNames.META, FilterTypeNames.APPEND, FilterTypeNames.PREPEND };

	protected FilterAction dispatchAction;

	protected FilterAction continueAction;

	protected FilterAction errorAction;

	protected FilterAction substitutionAction;

	protected FilterAction adviceAction;

	protected FilterAction metaAction;

	protected FilterTypeMapping mapping;

	protected DataStore repository;

	protected boolean allowLegacyCustom;

	public DefaultFilterFactory(DataStore repo)
	{
		repository = repo;
	}

	public DefaultFilterFactory(FilterTypeMapping ftm, DataStore repo)
	{
		mapping = ftm;
		repository = repo;
	}

	/**
	 * Add all legacy filter types and permit creation of the legacy custom
	 * filter types
	 * 
	 * @throws UnsupportedFilterTypeException
	 */
	public void addLegacyFilterTypes() throws UnsupportedFilterTypeException
	{
		allowLegacyCustom = true;
		createFilterTypes(LEGACY);
	}

	public void setTypeMapping(FilterTypeMapping tm)
	{
		mapping = tm;
	}

	public void setAllowLegacyCustomFilters(boolean value)
	{
		allowLegacyCustom = value;
	}

	public boolean allowLegacyCustomFilters()
	{
		return allowLegacyCustom;
	}

	/**
	 * Create the given list of default filter types
	 * 
	 * @param types
	 */
	public void createFilterTypes(String[] types) throws UnsupportedFilterTypeException
	{
		for (String type : types)
		{
			createFilterType(type);
		}
	}

	/**
	 * Creates a known/default filter type
	 * 
	 * @param type
	 */
	public void createFilterType(String type) throws UnsupportedFilterTypeException
	{
		if (FilterTypeNames.DISPATCH.equals(type))
		{
			addDispatchFilterType();
		}
		else if (FilterTypeNames.SEND.equals(type))
		{
			addSendFilterType();
		}
		else if (FilterTypeNames.AFTER.equals(type))
		{
			addAfterFilterType();
		}
		else if (FilterTypeNames.APPEND.equals(type))
		{
			addAppendFilterType();
		}
		else if (FilterTypeNames.BEFORE.equals(type))
		{
			addBeforeFilterType();
		}
		else if (FilterTypeNames.ERROR.equals(type))
		{
			addErrorFilterType();
		}
		else if (FilterTypeNames.META.equals(type))
		{
			addMetaFilterType();
		}
		else if (FilterTypeNames.PREPEND.equals(type))
		{
			addPrependFilterType();
		}
		else if (FilterTypeNames.SUBSTITUTION.equals(type))
		{
			addSubstitutionFilterType();
		}
	}

	/**
	 * Creates a legacy custom filter type. All actions are continue actions.
	 * 
	 * @param name
	 * @return
	 */
	public FilterType createLegacyCustomFilterType(String name) throws UnsupportedFilterTypeException
	{
		if (!allowLegacyCustom)
		{
			return null;
		}
		LegacyCustomFilterType custom = new LegacyCustomFilterType(name);
		// use 'continueActions' because these kind of CustomFilters have
		// unknown behaviors, unable to reason about them.
		try
		{
			custom.setAcceptCallAction(getContinueAction());
			custom.setRejectCallAction(getContinueAction());
			custom.setAcceptReturnAction(getContinueAction());
			custom.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.CUSTOM, e);
		}
		repository.addObject(custom);
		if (mapping != null)
		{
			mapping.registerFilterType(custom);
		}
		return custom;
	}

	// Filter Actions

	/**
	 * Get a standard filter action with the given name.
	 */
	public FilterAction getFilterAction(String name) throws UnsupportedFilterActionException
	{
		if (FilterActionNames.DISPATCH_ACTION.equals(name))
		{
			return getDispatchAction();
		}
		else if (FilterActionNames.CONTINUE_ACTION.equals(name))
		{
			return getContinueAction();
		}
		else if (FilterActionNames.ADVICE_ACTION.equals(name))
		{
			return getAdviceAction();
		}
		else if (FilterActionNames.ERROR_ACTION.equals(name))
		{
			return getErrorAction();
		}
		else if (FilterActionNames.SUBSTITUTION_ACTION.equals(name))
		{
			return getSubstitutionAction();
		}
		else if (FilterActionNames.META_ACTION.equals(name))
		{
			return getMetaAction();
		}
		return null;
	}

	public FilterAction getDispatchAction() throws UnsupportedFilterActionException
	{
		if (dispatchAction != null)
		{
			return dispatchAction;
		}
		dispatchAction = new FilterAction();
		dispatchAction.setName(FilterActionNames.DISPATCH_ACTION);
		dispatchAction.setFullName(FilterActionNames.DISPATCH_ACTION);
		dispatchAction.setFlowBehaviour(FilterAction.FLOW_RETURN);
		dispatchAction.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
		dispatchAction.setResourceOperations("arg.read;message.dispatch;return.read;");
		dispatchAction.setCreateJPC(false);
		repository.addObject(dispatchAction);
		return dispatchAction;
	}

	public FilterAction getContinueAction() throws UnsupportedFilterActionException
	{
		if (continueAction != null)
		{
			return continueAction;
		}
		continueAction = new FilterAction();
		continueAction.setName(FilterActionNames.CONTINUE_ACTION);
		continueAction.setFullName(FilterActionNames.CONTINUE_ACTION);
		continueAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		continueAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		continueAction.setCreateJPC(false);
		repository.addObject(continueAction);
		return continueAction;
	}

	public FilterAction getErrorAction() throws UnsupportedFilterActionException
	{
		if (errorAction != null)
		{
			return errorAction;
		}
		errorAction = new FilterAction();
		errorAction.setName(FilterActionNames.ERROR_ACTION);
		errorAction.setFullName(FilterActionNames.ERROR_ACTION);
		errorAction.setFlowBehaviour(FilterAction.FLOW_EXIT);
		errorAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		errorAction.setResourceOperations("message.discard;return.discard;arg.discard");
		errorAction.setCreateJPC(false);
		repository.addObject(errorAction);
		return errorAction;
	}

	public FilterAction getSubstitutionAction() throws UnsupportedFilterActionException
	{
		if (substitutionAction != null)
		{
			return substitutionAction;
		}
		substitutionAction = new FilterAction();
		substitutionAction.setName(FilterActionNames.SUBSTITUTION_ACTION);
		substitutionAction.setFullName(FilterActionNames.SUBSTITUTION_ACTION);
		substitutionAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		substitutionAction.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
		substitutionAction.setCreateJPC(false);
		repository.addObject(substitutionAction);
		return substitutionAction;
	}

	public FilterAction getAdviceAction() throws UnsupportedFilterActionException
	{
		if (adviceAction != null)
		{
			return adviceAction;
		}
		adviceAction = new FilterAction();
		adviceAction.setName(FilterActionNames.ADVICE_ACTION);
		adviceAction.setFullName(FilterActionNames.ADVICE_ACTION);
		adviceAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		adviceAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		repository.addObject(adviceAction);
		return adviceAction;
	}

	public FilterAction getMetaAction() throws UnsupportedFilterActionException
	{
		if (metaAction != null)
		{
			return metaAction;
		}
		metaAction = new FilterAction();
		metaAction.setName(FilterActionNames.META_ACTION);
		metaAction.setFullName(FilterActionNames.META_ACTION);
		metaAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		// TODO: this should actually be MESSAGE_ANY, but it breaks various
		// things during the compose* compile process
		metaAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		repository.addObject(metaAction);
		return metaAction;
	}

	// Filter Types

	public void addDispatchFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.DISPATCH);
		try
		{
			type.setAcceptCallAction(getDispatchAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getContinueAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.DISPATCH, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addSendFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.SEND);
		try
		{
			type.setAcceptCallAction(getDispatchAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getContinueAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.SEND, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addMetaFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.META);
		try
		{
			type.setAcceptCallAction(getMetaAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getMetaAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.META, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addErrorFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.ERROR);
		try
		{
			type.setAcceptCallAction(getContinueAction());
			type.setRejectCallAction(getErrorAction());
			type.setAcceptReturnAction(getContinueAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.ERROR, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addBeforeFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.BEFORE);
		try
		{
			type.setAcceptCallAction(getAdviceAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getContinueAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.BEFORE, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addAfterFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.AFTER);
		try
		{
			type.setAcceptCallAction(getContinueAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getAdviceAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.AFTER, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addSubstitutionFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.SUBSTITUTION);
		try
		{
			type.setAcceptCallAction(getSubstitutionAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getContinueAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.SUBSTITUTION, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addPrependFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.PREPEND);
		try
		{
			type.setAcceptCallAction(getAdviceAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getContinueAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.PREPEND, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	public void addAppendFilterType() throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.APPEND);
		try
		{
			type.setAcceptCallAction(getContinueAction());
			type.setRejectCallAction(getContinueAction());
			type.setAcceptReturnAction(getAdviceAction());
			type.setRejectReturnAction(getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(FilterTypeNames.APPEND, e);
		}
		repository.addObject(type);
		if (mapping != null)
		{
			mapping.registerFilterType(type);
		}
	}

	/**
	 * Exception thrown when a given filter type is not supported by the current
	 * factory/platform
	 * 
	 * @author Michiel Hendriks
	 */
	public static class UnsupportedFilterTypeException extends Exception
	{
		private static final long serialVersionUID = 3847929384310561849L;

		protected String filterType;

		public UnsupportedFilterTypeException(String type)
		{
			super();
			filterType = type;
		}

		public UnsupportedFilterTypeException(String type, Throwable cause)
		{
			super(cause);
			filterType = type;
		}

		public String getType()
		{
			return filterType;
		}

		@Override
		public String toString()
		{
			return String.format("The filter type \"%s\" is not supported.", filterType);
		}
	}

	/**
	 * Exception thrown when a given filter action is not supported by the
	 * current factory/platform
	 * 
	 * @author Michiel Hendriks
	 */
	public static class UnsupportedFilterActionException extends Exception
	{
		private static final long serialVersionUID = 120051574939262658L;

		protected String filterAction;

		public UnsupportedFilterActionException(String type)
		{
			super();
			filterAction = type;
		}

		public UnsupportedFilterActionException(String type, Throwable cause)
		{
			super(cause);
			filterAction = type;
		}

		public String getType()
		{
			return filterAction;
		}

		@Override
		public String toString()
		{
			return String.format("The filter action \"%s\" is not supported.", filterAction);
		}
	}
}
