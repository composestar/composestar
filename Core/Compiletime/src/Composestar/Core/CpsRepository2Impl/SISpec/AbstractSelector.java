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

package Composestar.Core.CpsRepository2Impl.SISpec;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.CpsRepository2.SISpec.Selector;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * Common implementation for all selector types.
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractSelector extends AbstractQualifiedRepositoryEntity implements Selector
{
	private static final long serialVersionUID = 8013970018196597012L;

	/**
	 * The selected program elements
	 */
	protected Set<ProgramElement> selection;

	/**
	 * @param entityName The name of the selector
	 * @throws NullPointerException Thrown when the selector name is null
	 * @throws IllegalArgumentException Thrown when the selector name is empty
	 */
	protected AbstractSelector(String entityName) throws NullPointerException, IllegalArgumentException
	{
		super(entityName);
		selection = new HashSet<ProgramElement>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SISpec.Selector#getSelection()
	 */
	public Collection<ProgramElement> getSelection()
	{
		return Collections.unmodifiableSet(selection);
	}

	/**
	 * Set the selector's value. The previous value is overwritten.
	 * 
	 * @param elements The selector's new value.
	 * @throws NullPointerException Thrown when the provided list is null.
	 */
	public void setSelection(Collection<ProgramElement> elements) throws NullPointerException
	{
		if (elements == null)
		{
			throw new NullPointerException();
		}
		selection = new HashSet<ProgramElement>(elements);
	}
}
