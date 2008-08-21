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

package Composestar.Core.CpsRepository2.FilterElements;

import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;

/**
 * This matching expression element uses the result of a condition defined in
 * the filter module.
 * 
 * @author Michiel Hendriks
 */
public interface MECondition extends MatchingExpression, Instantiatable<MECondition>
{
	/**
	 * Sets the filter module condition.
	 * 
	 * @param cond The condition to associate
	 * @throws NullPointerException Thrown when the condition is null.
	 */
	void setCondition(Condition cond) throws NullPointerException;

	/**
	 * @return The associated condition
	 */
	Condition getCondition();
}
