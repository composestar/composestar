/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.CpsProgramRepository.Legacy;

import Composestar.Core.COPPER2.FilterTypeMapping;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Inserts the legacy filter types to the repository to serve as legacy glue for
 * the reasoning about the old standard filter types. These should only be used
 * for legacy versions of the ports, new versions should use proper filter
 * actions.
 * 
 * @author Michiel Hendriks
 */
public final class LegacyFilterTypes
{
	public static final String RESOURCE_KEY = "Repository.LegacyFilterTypes";

	/**
	 * If true legacy filter types will be used
	 */
	public static boolean useLegacyFilterTypes;

	private FilterAction dispatchAction;

	private FilterAction continueAction;

	private FilterAction errorAction;

	// private FilterAction substitutionAction;

	private FilterAction adviceAction;

	private FilterAction metaAction;

	private FilterTypeMapping mapping;

	private DataStore repository;

	public LegacyFilterTypes(FilterTypeMapping ftm, DataStore repo)
	{
		mapping = ftm;
		repository = repo;
		addLegacyFilterTypes();
	}

	private void addLegacyFilterTypes()
	{
		dispatchAction = createDispatchAction();
		continueAction = createContinueAction();
		errorAction = createErrorAction();
		// substitutionAction = createSubstitutionAction();
		adviceAction = createAdviceAction();
		metaAction = createMetaAction();

		addDispatchFilterType();
		addSendFilterType();
		addErrorFilterType();
		addBeforeFilterType();
		addAfterFilterType();
		addSubstitutionFilterType();
		addPrependFilterType();
		addAppendFilterType();
		addMetaFilterType();
	}

	public FilterType createCustomFilterType(String name)
	{
		LegacyCustomFilterType custom = new LegacyCustomFilterType(name);
		// use 'continueActions' because these kind of CustomFilters have
		// unknown behaviors, unable to reason about them.
		custom.setAcceptCallAction(continueAction);
		custom.setRejectCallAction(continueAction);
		custom.setAcceptReturnAction(continueAction);
		custom.setRejectReturnAction(continueAction);
		repository.addObject(custom);
		mapping.registerFilterType(custom);
		return custom;
	}

	// Filter Actions

	private FilterAction createDispatchAction()
	{
		FilterAction action = new FilterAction();
		action.setName("DispatchAction");
		action.setFullName("DispatchAction");
		action.setFlowBehaviour(FilterAction.FLOW_RETURN);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
		action.setResourceOperations("arg.read;message.dispatch;return.read;");
		repository.addObject(action);
		return action;
	}

	private FilterAction createContinueAction()
	{
		FilterAction action = new FilterAction();
		action.setName("ContinueAction");
		action.setFullName("ContinueAction");
		action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		repository.addObject(action);
		return action;
	}

	private FilterAction createErrorAction()
	{
		FilterAction action = new FilterAction();
		action.setName("ErrorAction");
		action.setFullName("ErrorAction");
		action.setFlowBehaviour(FilterAction.FLOW_EXIT);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		action.setResourceOperations("message.discard;return.discard;arg.discard");
		repository.addObject(action);
		return action;
	}

	// private static FilterAction createSubstitutionAction()
	// {
	// FilterAction action = new FilterAction();
	// action.setName("SubstitutionAction");
	// action.setFullName("SubstitutionAction");
	// action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
	// action.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
	// repository.addObject(action);
	// return action;
	// }

	private FilterAction createAdviceAction()
	{
		FilterAction action = new FilterAction();
		action.setName("AdviceAction");
		action.setFullName("AdviceAction");
		action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		repository.addObject(action);
		return action;
	}

	private FilterAction createMetaAction()
	{
		FilterAction action = new FilterAction();
		action.setName("MetaAction");
		action.setFullName("MetaAction");
		action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		// TODO: this should actually be MESSAGE_ANY, but it breaks various
		// things during the compose* compile process
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		repository.addObject(action);
		return action;
	}

	// Filter Types

	private void addDispatchFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.DISPATCH);
		type.setAcceptCallAction(dispatchAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
		repository.addObject(type);
		mapping.registerFilterType(type);
	}

	private void addSendFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.SEND);
		type.setAcceptCallAction(dispatchAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
		repository.addObject(type);
		mapping.registerFilterType(type);
	}

	private void addMetaFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.META);
		type.setAcceptCallAction(metaAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(metaAction);
		type.setRejectReturnAction(continueAction);
		repository.addObject(type);
		mapping.registerFilterType(type);
	}

	private void addErrorFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.ERROR);
		type.setAcceptCallAction(continueAction);
		type.setRejectCallAction(errorAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
		repository.addObject(type);
		mapping.registerFilterType(type);
	}

	private void addBeforeFilterType()
	{
	// does not exist in the legacy system
	// FilterType type = new FilterType();
	// type.setType(FilterTypeNames.BEFORE);
	// type.setAcceptCallAction(adviceAction);
	// type.setRejectCallAction(continueAction);
	// type.setAcceptReturnAction(continueAction);
	// type.setRejectReturnAction(continueAction);
	// repository.addObject(type);
	// mapping.registerFilterType(type);
	}

	private void addAfterFilterType()
	{
	// does not exist in the legacy system
	// FilterType type = new FilterType();
	// type.setType(FilterTypeNames.AFTER);
	// type.setAcceptCallAction(continueAction);
	// type.setRejectCallAction(continueAction);
	// type.setAcceptReturnAction(adviceAction);
	// type.setRejectReturnAction(continueAction);
	// repository.addObject(type);
	// mapping.registerFilterType(type);
	}

	private void addSubstitutionFilterType()
	{
	// does not exist in the legacy system
	// FilterType type = new FilterType();
	// type.setType(FilterTypeNames.SUBSTITUTION);
	// type.setAcceptCallAction(substitutionAction);
	// type.setRejectCallAction(continueAction);
	// type.setAcceptReturnAction(continueAction);
	// type.setRejectReturnAction(continueAction);
	// repository.addObject(type);
	// mapping.registerFilterType(type);
	}

	private void addPrependFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.PREPEND);
		type.setAcceptCallAction(adviceAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
		repository.addObject(type);
		mapping.registerFilterType(type);
	}

	private void addAppendFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.APPEND);
		type.setAcceptCallAction(continueAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(adviceAction);
		type.setRejectReturnAction(continueAction);
		repository.addObject(type);
		mapping.registerFilterType(type);
	}
}
