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

package Composestar.Core.CpsRepository2Impl.SISpec;

/**
 * A predicate selector that contains prolog expression to query the language
 * model
 * 
 * @author Michiel Hendriks
 */
public class PredicateSelector extends AbstractSelector
{
	private static final long serialVersionUID = -7241709780425294120L;

	/**
	 * The prolog term to use as the result
	 */
	protected String resultTerm;

	/**
	 * The prolog expression
	 */
	protected String expression;

	/**
	 * @param entityName The name of the selector
	 * @param resultName The term from the expression that contains the
	 *            requested results
	 * @param predicateExpression The prolog expression to execute
	 * @throws NullPointerException Thrown when any of the arguments are null
	 * @throws IllegalArgumentException Thrown when any of the arguments are
	 *             empty
	 */
	public PredicateSelector(String entityName, String resultName, String predicateExpression)
			throws NullPointerException, IllegalArgumentException
	{
		super(entityName);
		if (resultName == null)
		{
			throw new NullPointerException("Result name can not be null");
		}
		if (resultName.length() == 0)
		{
			throw new IllegalArgumentException("Result name can not be empty");
		}
		if (predicateExpression == null)
		{
			throw new NullPointerException("Predicate expression can not be null");
		}
		if (predicateExpression.length() == 0)
		{
			throw new IllegalArgumentException("Predicate expression can not be empty");
		}
		resultTerm = resultName;
		expression = predicateExpression;
	}

	/**
	 * @return The term that contains the result
	 */
	public String getResultTerm()
	{
		return resultTerm;
	}

	/**
	 * @return The prolog expression that should be executed
	 */
	public String getExpression()
	{
		return expression;
	}
}
