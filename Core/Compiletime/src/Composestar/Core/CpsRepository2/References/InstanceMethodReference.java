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

package Composestar.Core.CpsRepository2.References;

import Composestar.Core.CpsRepository2.InstanceContextProvider;

/**
 * An method reference with a context. This can be used by Externals and
 * Conditions when they refer to a method of an Internal or External.
 * InstanceMethodReferences have a read-only type reference. The
 * {@link MethodReference#getTypeReference()} should return the value of
 * {@link InstanceContextProvider#getTypeReference()}.
 * 
 * @author Michiel Hendriks
 */
public interface InstanceMethodReference extends MethodReference
{
	/**
	 * @return The context on which this method should be executed.
	 */
	InstanceContextProvider getContext();

	/**
	 * @param context The context to set. When null the current context is
	 *            removed. In this case this instance will behave like a normal
	 *            MethodReference.
	 */
	void setContext(InstanceContextProvider context);
}
