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

package Composestar.Core.CpsRepository2.SISpec;

import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.References.MethodReference;

/**
 * A condition used in the superimposition block. It is used for either
 * conditional superimposition (as of 2008-08-19 not yet implemented) or
 * conditional filter module execution (through the cond(..) constraint).
 * 
 * @author Michiel Hendriks
 */
public interface SICondition extends QualifiedRepositoryEntity
{
	/**
	 * @return The reference to the method which produces the boolean value for
	 *         this condition.
	 */
	MethodReference getMethodReference();

	/**
	 * Sets the reference to a method that should be used to retrieve the
	 * boolean value for this condition. Only static methods can be used.
	 * 
	 * @param ref The reference to a method to use
	 * @throws NullPointerException Thrown when the passed reference is null
	 * @throws IllegalArgumentException Thrown when an InstanceMethodReference
	 *             is passed as argument.
	 */
	void setMethodReference(MethodReference ref) throws NullPointerException, IllegalArgumentException;
}
