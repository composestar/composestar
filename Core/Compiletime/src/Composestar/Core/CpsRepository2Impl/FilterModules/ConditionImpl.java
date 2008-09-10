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

package Composestar.Core.CpsRepository2Impl.FilterModules;

import Composestar.Core.CpsRepository2.FilterModules.Condition;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * Implementation of the condition interface
 * 
 * @author Michiel Hendriks
 */
public class ConditionImpl extends AbstractQualifiedRepositoryEntity implements Condition
{
	private static final long serialVersionUID = -952552089739981114L;

	/**
	 * The method this condition refers to. Can be a static method or a instance
	 * method.
	 */
	protected MethodReference methodReference;

	/**
	 * @param entityName The name of the condition
	 * @throws NullPointerException Thrown when the name is null
	 * @throws IllegalArgumentException Thrown when the name is empty
	 */
	public ConditionImpl(String entityName) throws NullPointerException, IllegalArgumentException
	{
		super(entityName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Condition#getMethodReference
	 * ()
	 */
	public MethodReference getMethodReference()
	{
		return methodReference;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterModules.Condition#setMethodReference
	 * (Composestar.Core.CpsRepository2.References.MethodReference)
	 */
	public void setMethodReference(MethodReference ref) throws NullPointerException
	{
		if (ref == null)
		{
			throw new NullPointerException();
		}
		methodReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
	 * getProgramElement()
	 */
	public ProgramElement getProgramElement()
	{
		if (methodReference != null)
		{
			return methodReference.getReference();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public Condition newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
