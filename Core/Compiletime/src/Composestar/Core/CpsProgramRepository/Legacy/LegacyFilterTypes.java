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

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;

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
	private static final FilterAction dispatchAction = createDispatchAction();

	private static final FilterAction continueAction = createContinueAction();

	private static final FilterAction errorAction = createErrorAction();

	// private static final FilterAction substitutionAction =
	// createSubstitutionAction();

	private static final FilterAction adviceAction = createAdviceAction();

	private static final FilterAction metaAction = createMetaAction();

	/**
	 * If true legacy filter types will be used
	 */
	public static boolean useLegacyFilterTypes = false;

	public static void addLegacyFilterTypes()
	{
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

	public static FilterType createCustomFilterType(String name)
	{
		LegacyCustomFilterType custom = new LegacyCustomFilterType(name);
		// use 'continueActions' because these kind of CustomFilters have
		// unknown behaviors, unable to reason about them.
		custom.setAcceptCallAction(continueAction);
		custom.setRejectCallAction(continueAction);
		custom.setAcceptReturnAction(continueAction);
		custom.setRejectReturnAction(continueAction);
		return custom;
	}

	// Filter Actions

	private static FilterAction createDispatchAction()
	{
		FilterAction action = new FilterAction();
		action.setName("DispatchAction");
		action.setFullName("DispatchAction");
		action.setFlowBehaviour(FilterAction.FLOW_RETURN);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
		return action;
	}

	private static FilterAction createContinueAction()
	{
		FilterAction action = new FilterAction();
		action.setName("ContinueAction");
		action.setFullName("ContinueAction");
		action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		return action;
	}

	private static FilterAction createErrorAction()
	{
		FilterAction action = new FilterAction();
		action.setName("ErrorAction");
		action.setFullName("ErrorAction");
		action.setFlowBehaviour(FilterAction.FLOW_EXIT);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		return action;
	}

	// private static FilterAction createSubstitutionAction()
	// {
	// FilterAction action = new FilterAction();
	// action.setName("SubstitutionAction");
	// action.setFullName("SubstitutionAction");
	// action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
	// action.setMessageChangeBehaviour(FilterAction.MESSAGE_SUBSTITUTED);
	// return action;
	// }

	private static FilterAction createAdviceAction()
	{
		FilterAction action = new FilterAction();
		action.setName("AdviceAction");
		action.setFullName("AdviceAction");
		action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		return action;
	}

	private static FilterAction createMetaAction()
	{
		FilterAction action = new FilterAction();
		action.setName("MetaAction");
		action.setFullName("MetaAction");
		action.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		// TODO: this should actually be MESSAGE_ANY, but it breaks various things during the compose* compile process
		action.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		return action;
	}

	// Filter Types

	private static void addDispatchFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.DISPATCH);
		type.setAcceptCallAction(dispatchAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
	}

	private static void addSendFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.SEND);
		type.setAcceptCallAction(dispatchAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
	}

	private static void addMetaFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.META);
		type.setAcceptCallAction(metaAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(metaAction);
		type.setRejectReturnAction(continueAction);
	}

	private static void addErrorFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.ERROR);
		type.setAcceptCallAction(continueAction);
		type.setRejectCallAction(errorAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
	}

	private static void addBeforeFilterType()
	{
	// does not exist in the legacy system
	// FilterType type = new FilterType(); type.setType(FilterTypeNames.BEFORE);
	// type.setAcceptCallAction(adviceAction);
	// type.setRejectCallAction(continueAction);
	// type.setAcceptReturnAction(continueAction);
	// type.setRejectReturnAction(continueAction);
	}

	private static void addAfterFilterType()
	{
	// does not exist in the legacy system
	// FilterType type = new FilterType();
	// type.setType(FilterTypeNames.AFTER);
	// type.setAcceptCallAction(continueAction);
	// type.setRejectCallAction(continueAction);
	// type.setAcceptReturnAction(adviceAction);
	// type.setRejectReturnAction(continueAction);
	}

	private static void addSubstitutionFilterType()
	{
	// does not exist in the legacy system
	// FilterType type = new FilterType();
	// type.setType(FilterTypeNames.SUBSTITUTION);
	// type.setAcceptCallAction(substitutionAction);
	// type.setRejectCallAction(continueAction);
	// type.setAcceptReturnAction(continueAction);
	// type.setRejectReturnAction(continueAction);
	}

	private static void addPrependFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.PREPEND);
		type.setAcceptCallAction(adviceAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(continueAction);
		type.setRejectReturnAction(continueAction);
	}

	private static void addAppendFilterType()
	{
		FilterType type = new FilterType();
		type.setType(FilterTypeNames.APPEND);
		type.setAcceptCallAction(continueAction);
		type.setRejectCallAction(continueAction);
		type.setAcceptReturnAction(adviceAction);
		type.setRejectReturnAction(continueAction);
	}
}
