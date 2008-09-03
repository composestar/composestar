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

import Composestar.Core.CpsRepository2.InstanceContextProvider;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiatable;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.References.TypeReference;

/**
 * An external filter module variable. Externals are initialized using a
 * references method.
 * 
 * @author Michiel Hendriks
 */
public interface External extends FilterModuleVariable, InstanceContextProvider, Instantiatable<External>
{
	/**
	 * Sets the reference to the type of the external. The references type
	 * should have a default constructor.
	 * 
	 * @param ref The type of the external
	 * @throws NullPointerException Thrown when the type reference is null.
	 */
	void setTypeReference(TypeReference ref) throws NullPointerException;

	/**
	 * @return Return the reference to the method which is used to initialize
	 *         the external.
	 */
	MethodReference getMethodReference();

	/**
	 * Sets the initialization method. The initialization method is optional, if
	 * no method reference is set the default constructor of the external is
	 * called. In this case the external behaves identical to the internal.
	 * 
	 * @param ref The method reference to use as initialization method.
	 */
	void setMethodReference(MethodReference ref);
}
