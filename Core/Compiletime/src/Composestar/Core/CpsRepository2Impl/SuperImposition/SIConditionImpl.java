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

package Composestar.Core.CpsRepository2Impl.SuperImposition;

import Composestar.Core.CpsRepository2.References.InstanceMethodReference;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SuperImposition.SICondition;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * Basic implementation of the SICondition interface
 * 
 * @author "Michiel Hendriks"
 */
public class SIConditionImpl extends AbstractQualifiedRepositoryEntity implements SICondition
{
	private static final long serialVersionUID = 8574446072519553204L;

	/**
	 * Reference to the static method used for this condition.
	 */
	protected MethodReference methodReference;

	/**
	 * @param entityName
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public SIConditionImpl(String entityName) throws NullPointerException, IllegalArgumentException
	{
		super(entityName);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SICondition#
	 * getMethodReference()
	 */
	public MethodReference getMethodReference()
	{
		return methodReference;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.SICondition#
	 * setMethodReference
	 * (Composestar.Core.CpsRepository2.References.MethodReference)
	 */
	public void setMethodReference(MethodReference ref) throws NullPointerException, IllegalArgumentException
	{
		if (ref == null)
		{
			throw new NullPointerException();
		}
		if (ref instanceof InstanceMethodReference)
		{
			throw new IllegalArgumentException("InstanceMethodReference is not allowed");
		}
		methodReference = ref;
	}

}
