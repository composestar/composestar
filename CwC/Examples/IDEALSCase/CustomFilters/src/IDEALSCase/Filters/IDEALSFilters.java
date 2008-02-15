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

package IDEALSCase.Filters;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CKRET.Config.ConflictRule.RuleType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.CpsProgramRepository.Filters.UnsupportedFilterActionException;
import Composestar.Core.CpsProgramRepository.Filters.UnsupportedFilterTypeException;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.CodeGen.VoidFilterActionCodeGen;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.CwC.Filters.CustomCwCFilters;

/**
 * @author Michiel Hendriks
 */
public class IDEALSFilters implements CustomCwCFilters
{
	public static final String PARAMETER_CHECKING_FILTER = "ParameterChecking";

	public static final String PARAM_PRE_CHECK_ACTION = "PreParamCheckAction";

	public static final String PARAM_POST_CHECK_ACTION = "PostParamCheckAction";

	public static final String ERROR_PROPAGATION_FILTER = "ErrorPropagation";

	public static final String ERROR_PROPAGATION_ACTION = "ErrorPropagationAction";

	private FilterAction paramCheckPreAction;

	private FilterAction paramCheckPostAction;

	private FilterAction errorPropagationAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#getCodeGenerators()
	 */
	public Collection<FilterActionCodeGenerator<String>> getCodeGenerators()
	{
		String[] stubs = { PARAM_PRE_CHECK_ACTION, PARAM_POST_CHECK_ACTION, ERROR_PROPAGATION_ACTION };
		Set<FilterActionCodeGenerator<String>> facgs = new HashSet<FilterActionCodeGenerator<String>>();
		facgs.add(new VoidFilterActionCodeGen<String>(stubs));
		return facgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#registerFilters(Composestar.Core.RepositoryImplementation.DataStore,
	 *      Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory)
	 */
	public void registerFilters(DataStore repository, DefaultFilterFactory factory)
	{
		try
		{
			addParameterChecking(repository, factory);
			addErrorPropagation(repository, factory);
		}
		catch (UnsupportedFilterTypeException e)
		{
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CwC.Filters.CustomCwCFilters#registerSecretResources(Composestar.Core.CKRET.SECRETResources)
	 */
	public void registerSecretResources(SECRETResources resources)
	{
		Resource rsc = ResourceType.createResource("errorvariable", false);
		rsc.addVocabulary("read");
		rsc.addVocabulary("write");
		resources.addResource(rsc);
		try
		{
			ConflictRule cr = new ConflictRule(rsc, RuleType.Constraint, "(.*)write$",
					"a written errorvariable must be read");
			resources.addRule(cr);
			cr = new ConflictRule(rsc, RuleType.Constraint, "(write)(write)",
					"previous value of errorvariable overwritten");
			resources.addRule(cr);
		}
		catch (PatternParseException e)
		{
		}
	}

	public FilterAction getParamCheckPreAction(DataStore repository)
	{
		if (paramCheckPreAction != null)
		{
			return paramCheckPreAction;
		}
		paramCheckPreAction = new FilterAction();
		paramCheckPreAction.setName(PARAM_PRE_CHECK_ACTION);
		paramCheckPreAction.setFullName(PARAM_PRE_CHECK_ACTION);
		paramCheckPreAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		paramCheckPreAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		paramCheckPreAction.setResourceOperations("errorvariable.read;errorvariable.write");
		paramCheckPreAction.setCreateJPC(false);
		repository.addObject(paramCheckPreAction);
		return paramCheckPreAction;
	}

	public FilterAction getParamCheckPostAction(DataStore repository)
	{
		if (paramCheckPostAction != null)
		{
			return paramCheckPostAction;
		}
		paramCheckPostAction = new FilterAction();
		paramCheckPostAction.setName(PARAM_POST_CHECK_ACTION);
		paramCheckPostAction.setFullName(PARAM_POST_CHECK_ACTION);
		paramCheckPostAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		paramCheckPostAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		paramCheckPostAction.setResourceOperations("errorvariable.read;errorvariable.write");
		paramCheckPostAction.setCreateJPC(false);
		repository.addObject(paramCheckPostAction);
		return paramCheckPostAction;
	}

	public void addParameterChecking(DataStore repository, DefaultFilterFactory factory)
			throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(PARAMETER_CHECKING_FILTER);
		try
		{
			type.setAcceptCallAction(getParamCheckPreAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(factory.getContinueAction());
			// type.setAcceptReturnAction(getParamCheckPostAction(repository));
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(PARAMETER_CHECKING_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}

	public FilterAction getErrorPropagationAction(DataStore repository)
	{
		if (errorPropagationAction != null)
		{
			return errorPropagationAction;
		}
		errorPropagationAction = new FilterAction();
		errorPropagationAction.setName(ERROR_PROPAGATION_ACTION);
		errorPropagationAction.setFullName(ERROR_PROPAGATION_ACTION);
		errorPropagationAction.setFlowBehaviour(FilterAction.FLOW_CONTINUE);
		errorPropagationAction.setMessageChangeBehaviour(FilterAction.MESSAGE_ORIGINAL);
		errorPropagationAction
				.setResourceOperations("errorvariable.read;errorvariable.write;errorvariable.read;errorvariable.read");
		errorPropagationAction.setCreateJPC(false);
		repository.addObject(errorPropagationAction);
		return errorPropagationAction;
	}

	public void addErrorPropagation(DataStore repository, DefaultFilterFactory factory)
			throws UnsupportedFilterTypeException
	{
		FilterType type = new FilterType();
		type.setType(ERROR_PROPAGATION_FILTER);
		try
		{
			type.setAcceptCallAction(getErrorPropagationAction(repository));
			type.setRejectCallAction(factory.getContinueAction());
			type.setAcceptReturnAction(factory.getContinueAction());
			type.setRejectReturnAction(factory.getContinueAction());
		}
		catch (UnsupportedFilterActionException e)
		{
			throw new UnsupportedFilterTypeException(ERROR_PROPAGATION_FILTER, e);
		}
		repository.addObject(type);
		if (factory.getTypeMapping() != null)
		{
			factory.getTypeMapping().registerFilterType(type);
		}
	}
}
