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

package Composestar.Core.COPPER3;

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Filters.FilterTypeNames;
import Composestar.Core.CpsRepository2.Filters.FilterAction.FlowBehavior;
import Composestar.Core.CpsRepository2Impl.Filters.CustomFilterType;
import Composestar.Core.CpsRepository2Impl.Filters.FilterActionImpl;
import Composestar.Core.CpsRepository2Impl.Filters.PrimitiveFilterTypeImpl;

/**
 * Filter factory. Creates filters on the fly.
 * 
 * @author Michiel Hendriks
 */
public class FilterTypeFactory
{
	public static final String RESOURCE_KEY = "COPPER3.FilterTypeFactory";

	/**
	 * The default builtin filter types
	 */
	public static final String[] DEFAULT_FILTER_TYPES =
			{ FilterTypeNames.AFTER, FilterTypeNames.BEFORE, FilterTypeNames.DISPATCH, FilterTypeNames.ERROR,
					FilterTypeNames.SEND, FilterTypeNames.SUBSTITUTION, FilterTypeNames.META,
					FilterTypeNames.EXCEPTION, FilterTypeNames.VOID, };

	/**
	 * The language repository.
	 */
	protected Repository repository;

	/**
	 * Contains a lookup of names to filter type
	 */
	protected FilterTypeMapping mapping;

	/**
	 * If true custom filter types may be constructed on the fly.
	 */
	protected boolean allowCustomFilterTypeCreation;

	/**
	 * The continue action
	 */
	protected FilterActionImpl continueAction;

	/**
	 * The dispatch action
	 */
	protected FilterActionImpl dispatchAction;

	/**
	 * The send action
	 */
	protected FilterActionImpl sendAction;

	/**
	 * The error action
	 */
	protected FilterActionImpl errorAction;

	/**
	 * The advice action
	 */
	protected FilterActionImpl adviceAction;

	/**
	 * The meta action
	 */
	protected FilterActionImpl metaAction;

	/**
	 * Create the filter type factory. Use this constructor
	 * 
	 * @param repo
	 */
	public FilterTypeFactory(Repository repo)
	{
		repository = repo;
	}

	/**
	 * If true allow custom filter types to be created on the fly. These custom
	 * filter types will contain all continue actions, it is strongly advised to
	 * use an other module to resolve the true behavior of these filter type
	 * (like the FITER module).
	 * 
	 * @param value
	 */
	public void setAllowCustomFilterTypeCreation(boolean value)
	{
		allowCustomFilterTypeCreation = value;
	}

	/**
	 * Create a custom filter type. This method is used by COPPER to create
	 * custom filter types during parsing of the source. If all available filter
	 * types (including custom types) are known before parsing they should have
	 * been registered with the system before hand.
	 * 
	 * @param name The name of the filter
	 * @return returns the custom filter type when allowed, if on-the-fly
	 *         creation of custom filter types is now allowed it returns null.
	 */
	public FilterType createCustomFilterType(String name)
	{
		if (!allowCustomFilterTypeCreation)
		{
			return null;
		}
		if (name.indexOf('.') > -1)
		{
			// FIXME: error custom filter types may not contain a "."
			return null;
		}
		CustomFilterType res = new CustomFilterType(name);
		res.setAcceptCallAction(getContinueAction());
		res.setAcceptReturnAction(getContinueAction());
		res.setRejectCallAction(getContinueAction());
		res.setRejectReturnAction(getContinueAction());
		if (mapping != null)
		{
			mapping.registerFilterType(res);
		}
		repository.add(res);
		return res;
	}

	/**
	 * @param filterTypes
	 */
	public void setTypeMapping(FilterTypeMapping filterTypes)
	{
		mapping = filterTypes;
	}

	/**
	 * @return The continue action
	 */
	public FilterAction getContinueAction()
	{
		if (continueAction != null)
		{
			return continueAction;
		}
		continueAction = new FilterActionImpl(FilterActionNames.CONTINUE_ACTION);
		repository.add(continueAction);
		return continueAction;
	}

	/**
	 * @return The dispatch action
	 */
	public FilterAction getDispatchAction()
	{
		if (dispatchAction != null)
		{
			return dispatchAction;
		}
		dispatchAction = new FilterActionImpl(FilterActionNames.DISPATCH_ACTION);
		repository.add(dispatchAction);
		dispatchAction.setFlowBehavior(FlowBehavior.RETURN);
		dispatchAction.setResourceOperations("arg.read;message.dispatch;return.read");
		return dispatchAction;
	}

	/**
	 * @return The send action
	 */
	public FilterAction getSendAction()
	{
		if (sendAction != null)
		{
			return sendAction;
		}
		sendAction = new FilterActionImpl(FilterActionNames.SEND_ACTION);
		repository.add(sendAction);
		sendAction.setFlowBehavior(FlowBehavior.RETURN);
		sendAction.setResourceOperations("arg.read;sender.write;message.dispatch;return.read");
		return sendAction;
	}

	/**
	 * @return The error action
	 */
	public FilterAction getErrorAction()
	{
		if (errorAction != null)
		{
			return errorAction;
		}
		errorAction = new FilterActionImpl(FilterActionNames.ERROR_ACTION);
		repository.add(errorAction);
		errorAction.setFlowBehavior(FlowBehavior.EXIT);
		errorAction.setResourceOperations("message.discard;return.discard;arg.discard");
		return errorAction;
	}

	/**
	 * @return The advice action
	 */
	public FilterAction getAdviceAction()
	{
		if (adviceAction != null)
		{
			return adviceAction;
		}
		adviceAction = new FilterActionImpl(FilterActionNames.ADVICE_ACTION);
		repository.add(adviceAction);
		adviceAction.setFlowBehavior(FlowBehavior.CONTINUE);
		adviceAction.setJoinPointContextArgument(JoinPointContextArgument.FULL);
		return adviceAction;
	}

	/**
	 * @return The meta action
	 */
	public FilterAction getMetaAction()
	{
		if (metaAction != null)
		{
			return metaAction;
		}
		metaAction = new FilterActionImpl(FilterActionNames.META_ACTION);
		repository.add(metaAction);
		metaAction.setFlowBehavior(FlowBehavior.CONTINUE);
		return metaAction;
	}

	/**
	 * Create a certain default filter type. Should be called only once per
	 * filter type
	 * 
	 * @param typeName
	 */
	public void createDefaultFilterType(String typeName)
	{
		PrimitiveFilterTypeImpl flt = null;
		if (FilterTypeNames.DISPATCH.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.DISPATCH);
			flt.setAcceptCallAction(getDispatchAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.SEND.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.SEND);
			flt.setAcceptCallAction(getSendAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.EXCEPTION.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.EXCEPTION);
			flt.setAcceptCallAction(getErrorAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.ERROR.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.ERROR);
			flt.setAcceptCallAction(getContinueAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getErrorAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.BEFORE.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.BEFORE);
			flt.setAcceptCallAction(getAdviceAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.AFTER.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.AFTER);
			flt.setAcceptCallAction(getContinueAction());
			flt.setAcceptReturnAction(getAdviceAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.SUBSTITUTION.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.SUBSTITUTION);
			flt.setAcceptCallAction(getContinueAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.VOID.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.VOID);
			flt.setAcceptCallAction(getContinueAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}
		else if (FilterTypeNames.META.equalsIgnoreCase(typeName))
		{
			flt = new PrimitiveFilterTypeImpl(FilterTypeNames.META);
			flt.setAcceptCallAction(getMetaAction());
			flt.setAcceptReturnAction(getContinueAction());
			flt.setRejectCallAction(getContinueAction());
			flt.setRejectReturnAction(getContinueAction());
		}

		if (flt != null)
		{
			repository.add(flt);
			if (mapping != null)
			{
				mapping.registerFilterType(flt);
			}
		}
	}

	/**
	 * Create a collection of default types
	 * 
	 * @param typeNames
	 */
	public void createDefaultFilterTypes(String... typeNames)
	{
		for (String typeName : typeNames)
		{
			createDefaultFilterType(typeName);
		}
	}
}
