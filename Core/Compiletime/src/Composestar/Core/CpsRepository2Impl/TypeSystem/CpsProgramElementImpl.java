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

import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;
import Composestar.Core.LAMA.ProgramElement;

/**
 * Implementation for the CpsProgramElement interface
 * 
 * @author Michiel Hendriks
 */
public class CpsProgramElementImpl extends AbstractRepositoryEntity implements CpsProgramElement
{
	private static final long serialVersionUID = -3225610457932009460L;

	/**
	 * The associated program element
	 */
	protected ProgramElement programElement;

	/**
	 * @param pe The program element.
	 * @throws NullPointerException thrown when the program element is null
	 */
	public CpsProgramElementImpl(ProgramElement pe) throws NullPointerException
	{
		super();
		if (pe == null)
		{
			throw new NullPointerException("program element is null");
		}
		programElement = pe;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.TypeSystem.CpsProgramElement#
	 * getProgramElement()
	 */
	public ProgramElement getProgramElement()
	{
		return programElement;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable#compatible(Composestar
	 * .Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	public boolean compatible(CpsVariable other) throws UnsupportedOperationException
	{
		if (!(other instanceof CpsProgramElement))
		{
			return false;
		}
		CpsProgramElement o = (CpsProgramElement) other;
		if (programElement == null)
		{
			return o.getProgramElement() == null;
		}
		return programElement.equals(o.getProgramElement());
	}
}
