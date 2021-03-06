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

package Composestar.Core.CpsRepository2.TypeSystem;

/**
 * An object instance. Used for internals, externals, and message properties
 * like sender, server, inner, etc.
 * 
 * @author Michiel Hendriks
 */
public interface CpsObject extends CpsTypeProgramElement
{
	/**
	 * @return True when this is an inner object.
	 */
	boolean isInnerObject();

	/**
	 * @return True when this is a "self" CpsObject, meaning this concern. An
	 *         CpsObject can not be both inner and self.
	 */
	boolean isSelfObject();

	/**
	 * This method can only be used during interpretation when real object
	 * instances exist.
	 * 
	 * @return The object instance associated with this CpsObject.
	 * @throws IllegalStateException Thrown by CpsObject implementations that
	 *             are not interpreted.
	 */
	Object getObject() throws IllegalStateException;
}
