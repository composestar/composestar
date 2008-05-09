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

package Composestar.Core.FILTH2.Model;

/**
 * Creates constraints
 * 
 * @author Michiel Hendriks
 */
public class ConstraintFactory
{
	private ConstraintFactory()
	{}

	/**
	 * Create a new constraint with the given type.
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 * @throws ConstraintCreationException thrown when the constraint type could
	 *             not be found or when the arguments for the constraint are
	 *             incorrect.
	 */
	public static Constraint createConstraint(String type, Action... arguments) throws ConstraintCreationException
	{
		if (OrderingConstraint.NAME.equals(type))
		{
			if (arguments.length != 2)
			{
				throw new ConstraintCreationException(String.format("pre constraint takes 2 arguments, received %d",
						arguments.length));
			}
			return new OrderingConstraint(arguments[0], arguments[1]);
		}
		if (IncludeConstraint.NAME.equals(type))
		{
			if (arguments.length != 2)
			{
				throw new ConstraintCreationException(String.format(
						"include constraint takes 2 arguments, received %d", arguments.length));
			}
			return new IncludeConstraint(arguments[0], arguments[1]);
		}
		if (ExcludeConstraint.NAME.equals(type))
		{
			if (arguments.length != 2)
			{
				throw new ConstraintCreationException(String.format(
						"exclude constraint takes 2 arguments, received %d", arguments.length));
			}
			return new ExcludeConstraint(arguments[0], arguments[1]);
		}
		if (CondConstraint.NAME.equals(type))
		{
			if (arguments.length != 2)
			{
				throw new ConstraintCreationException(String.format("cond constraint takes 2 arguments, received %d",
						arguments.length));
			}
			return new CondConstraint(arguments[0], arguments[1]);
		}
		if (SkipConstraint.NAME.equals(type))
		{
			if (arguments.length != 3)
			{
				throw new ConstraintCreationException(String.format("skip constraint takes 3 arguments, received %d",
						arguments.length));
			}
			return new SkipConstraint(arguments[0], arguments[1], arguments[2]);
		}
		throw new ConstraintCreationException(String.format("Unknown constraint type: %s", type));
	}

	/**
	 * Exception thrown when a constraint could not be created.
	 * 
	 * @author Michiel Hendriks
	 */
	public static class ConstraintCreationException extends Exception
	{
		private static final long serialVersionUID = -8754581198577132478L;

		public ConstraintCreationException(String message)
		{
			super(message);
		}
	}
}
