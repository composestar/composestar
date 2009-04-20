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

import Composestar.Core.COPPER3.FilterTypeFactory;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2Impl.Filters.FilterActionImpl;
import Composestar.Core.CpsRepository2Impl.Filters.PrimitiveFilterTypeImpl;
import Composestar.Core.FIRE2.util.regex.PatternParseException;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.CodeGen.VoidFilterActionCodeGen;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.Resource;
import Composestar.Core.SECRET3.Model.RuleType;
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

	private FilterActionImpl paramCheckPreAction;

	private FilterActionImpl paramCheckPostAction;

	private FilterActionImpl errorPropagationAction;

	/*
	 * (non-Javadoc)
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
	 * @see
	 * Composestar.CwC.Filters.CustomCwCFilters#registerFilters(Composestar.
	 * Core.RepositoryImplementation.DataStore,
	 * Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory)
	 */
	public void registerFilters(Repository repository, FilterTypeFactory factory)
	{
		addParameterChecking(repository, factory);
		addErrorPropagation(repository, factory);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.CwC.Filters.CustomCwCFilters#registerSecretResources(Composestar
	 * .Core.CKRET.SECRETResources)
	 */
	public void registerSecretResources(SECRETResources resources)
	{
		Resource rsc = new Resource("errorvariable");
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

	public FilterAction getParamCheckPreAction(Repository repository)
	{
		if (paramCheckPreAction != null)
		{
			return paramCheckPreAction;
		}
		paramCheckPreAction = new FilterActionImpl(PARAM_PRE_CHECK_ACTION);
		paramCheckPreAction.setResourceOperations("errorvariable.read;errorvariable.write");
		repository.add(paramCheckPreAction);
		return paramCheckPreAction;
	}

	public FilterAction getParamCheckPostAction(Repository repository)
	{
		if (paramCheckPostAction != null)
		{
			return paramCheckPostAction;
		}
		paramCheckPostAction = new FilterActionImpl(PARAM_POST_CHECK_ACTION);
		paramCheckPostAction.setResourceOperations("errorvariable.read;errorvariable.write");
		repository.add(paramCheckPostAction);
		return paramCheckPostAction;
	}

	public void addParameterChecking(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(PARAMETER_CHECKING_FILTER);
		type.setAcceptCallAction(getParamCheckPreAction(repository));
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(factory.getContinueAction());
		// type.setAcceptReturnAction(getParamCheckPostAction(repository));
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}

	public FilterAction getErrorPropagationAction(Repository repository)
	{
		if (errorPropagationAction != null)
		{
			return errorPropagationAction;
		}
		errorPropagationAction = new FilterActionImpl(ERROR_PROPAGATION_ACTION);
		errorPropagationAction
				.setResourceOperations("errorvariable.read;errorvariable.write;errorvariable.read;errorvariable.read");
		repository.add(errorPropagationAction);
		return errorPropagationAction;
	}

	public void addErrorPropagation(Repository repository, FilterTypeFactory factory)
	{
		PrimitiveFilterTypeImpl type = new PrimitiveFilterTypeImpl(ERROR_PROPAGATION_FILTER);
		type.setAcceptCallAction(getErrorPropagationAction(repository));
		type.setRejectCallAction(factory.getContinueAction());
		type.setAcceptReturnAction(factory.getContinueAction());
		type.setRejectReturnAction(factory.getContinueAction());
		repository.add(type);
	}
}
