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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.LAMA.Type;

/**
 * @author Michiel Hendriks
 */
// TODO: implement
public class CpsObjectImpl extends CpsTypeProgramElementImpl implements CpsObject
{
	private static final long serialVersionUID = -5151470511518075199L;

	/**
	 * @param type
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(Type type) throws NullPointerException
	{
		super(type);
	}

	/**
	 * @param ref
	 * @throws NullPointerException
	 */
	public CpsObjectImpl(TypeReference ref) throws NullPointerException
	{
		super(ref);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsObject#getInstance()
	 */
	public Object getInstance()
	{
		return null;
	}
}
