/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
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
package Composestar.Core.INLINE.lowlevel;

import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.LAMA.MethodInfo;

/**
 * This strategy is called by the LowLevelInliner to do the actual code
 * generation.
 * 
 * @author Arjan
 */
public interface LowLevelInlineStrategy
{
	/**
	 * Called to indicate that the inlining starts.
	 * 
	 * @param filterSet The filterset
	 * @param method The method for which inlining is done
	 * @param argReferences
	 */
	void startInline(List<ImposedFilterModule> filterSet, MethodInfo method);

	/**
	 * Called when inlining is done.
	 */
	void endInline();

	/**
	 * Called to indicate the start of a filterblock.
	 * 
	 * @param filter The filter
	 * @param jumpLabel The label of this filterblock
	 */
	void startFilter(Filter filter, int jumpLabel);

	/**
	 * Called to indicate the end of a filterblock.
	 */
	void endFilter();

	/**
	 * Called to indicate that a condition needs to be evaluated. Currently used
	 * only for filter module conditions
	 * 
	 * @param condition
	 */
	void evalConditionMethod(MethodReference condition, int jumpLabel);

	/**
	 * Called to indicate that a conditionexpression needs to be evaluated
	 * 
	 * @param condition The conditionexpression
	 */
	void evalMatchingExpr(MatchingExpression condition);

	/**
	 * The start of the truebranch after a condition expression evaluation.
	 */
	void beginTrueBranch();

	/**
	 * The end of the truebranch.
	 */
	void endTrueBranch();

	/**
	 * The start of the falsebranch after a condition expression evaluation.
	 */
	void beginFalseBranch();

	/**
	 * The end of the falsebranch.
	 */
	void endFalseBranch();

	/**
	 * Called when a jump needs to be done to the given jumplabel. When the
	 * jumplabel is -1, this indicates a jump to the end of the filtercode.
	 * 
	 * @param jumpLabel
	 */
	void jump(int jumpLabel);

	/**
	 * Called when the code for a certain filteraction needs to be generated.
	 * 
	 * @param state The executionstate corresponding with the filteraction.
	 * @param filterArgs the filter argument values, could be an empty set.
	 */
	void generateAction(ExecutionState state, Collection<CanonAssignment> filterArgs, List<String> resourceOps);

	/**
	 * Start of the processing of a matched filter element
	 * 
	 * @param matchedFilterElement
	 */
	void startFilterElement(FilterElement matchedFilterElement);

	/**
	 * End of the processing of a matched filter element
	 */
	void endFilterElement();
}
