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

package Composestar.Core.CpsRepository2.FilterModules;

import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;

/**
 * A condition can be used in the matching expression.
 * 
 * @author Michiel Hendriks
 */
public interface Condition extends FilterModuleVariable, CpsProgramElement, Instantiatable<Condition>
{
	/**
	 * @return Return the reference to the method which is called when this
	 *         condition is encountered.
	 */
	MethodReference getMethodReference();

	/**
	 * Sets the method to associate with this condition.
	 * 
	 * @param ref A reference to the method to use.
	 * @throws NullPointerException Thrown when the type reference is null.
	 */
	void setMethodReference(MethodReference ref) throws NullPointerException;
}
