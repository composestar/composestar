/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Core.CpsRepository2Impl.SISpec.Constraints;

import java.util.List;

import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintFactory;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValue;

/**
 * @author Michiel Hendriks
 */
public final class ConstraintFactoryImpl implements ConstraintFactory
{

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintFactory#
	 * createConstraint(java.lang.String, java.util.List)
	 */
	public Constraint createConstraint(String name, List<ConstraintValue> args) throws NullPointerException,
			IllegalArgumentException, InstantiationException
	{
		if (name == null)
		{
			throw new NullPointerException("Name cannot be null");
		}
		if (name.length() == 0)
		{
			throw new IllegalArgumentException("No constraint name given");
		}
		if (args == null)
		{
			throw new NullPointerException("No argument list given");
		}
		if (args.size() == 0)
		{
			throw new IllegalArgumentException("Empty argument list");
		}

		// pre, exclude, include are quite similar
		if (OrderingConstraint.NAME.equals(name) || ExcludeConstraint.NAME.equals(name)
				|| IncludeConstraint.NAME.equals(name))
		{
			if (args.size() != 2)
			{
				throw new IllegalArgumentException(String.format("Constraint %s takes 2 arguments", name));
			}
			if (!(args.get(0) instanceof FilterModuleConstraintValue))
			{
				throw new IllegalArgumentException(String.format(
						"First argument of the %s constraint needs to be a filter module", name));
			}
			if (!(args.get(1) instanceof FilterModuleConstraintValue))
			{
				throw new IllegalArgumentException(String.format(
						"Second argument of the %s constraint needs to be a filter module", name));
			}

			if (OrderingConstraint.NAME.equals(name))
			{
				return new OrderingConstraint((FilterModuleConstraintValue) args.get(0),
						(FilterModuleConstraintValue) args.get(1));
			}
			else if (ExcludeConstraint.NAME.equals(name))
			{
				return new ExcludeConstraint((FilterModuleConstraintValue) args.get(0),
						(FilterModuleConstraintValue) args.get(1));
			}
			else if (IncludeConstraint.NAME.equals(name))
			{
				return new IncludeConstraint((FilterModuleConstraintValue) args.get(0),
						(FilterModuleConstraintValue) args.get(1));
			}
		}

		else if (CondConstraint.NAME.equals(name))
		{
			if (args.size() != 2)
			{
				throw new IllegalArgumentException(String.format("Constraint %s takes 2 arguments", name));
			}
			if (!(args.get(1) instanceof FilterModuleConstraintValue))
			{
				throw new IllegalArgumentException(String.format(
						"Second argument of the %s constraint needs to be a filter module", name));
			}

			return new CondConstraint(args.get(0), (FilterModuleConstraintValue) args.get(1));
		}

		else if (SkipConstraint.NAME.equals(name))
		{
			if (args.size() != 3)
			{
				throw new IllegalArgumentException(String.format("Constraint %s takes 2 arguments", name));
			}
			if (!(args.get(1) instanceof FilterModuleConstraintValue))
			{
				throw new IllegalArgumentException(String.format(
						"Second argument of the %s constraint needs to be a filter module", name));
			}

			return new SkipConstraint(args.get(0), (FilterModuleConstraintValue) args.get(1), args.get(2));
		}

		throw new InstantiationException(String.format("No constraint type with the name %s", name));
	}

}
