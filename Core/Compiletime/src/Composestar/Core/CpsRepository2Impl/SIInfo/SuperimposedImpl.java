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

package Composestar.Core.CpsRepository2Impl.SIInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class SuperimposedImpl extends AbstractRepositoryEntity implements Superimposed
{
	private static final long serialVersionUID = -1502207697152585068L;

	/**
	 * All imposed filter modules, in any order
	 */
	protected List<ImposedFilterModule> imposedFilterModules;

	/**
	 *  
	 */
	public SuperimposedImpl()
	{
		super();
		imposedFilterModules = new ArrayList<ImposedFilterModule>();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SIInfo.Superimposed#addFilterModule(
	 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule)
	 */
	public void addFilterModule(ImposedFilterModule ifm) throws NullPointerException
	{
		if (ifm == null)
		{
			throw new NullPointerException("Imposed filter module can not be null");
		}
		imposedFilterModules.add(ifm);
		ifm.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.Superimposed#getFilterModules()
	 */
	public List<ImposedFilterModule> getFilterModules()
	{
		return Collections.unmodifiableList(imposedFilterModules);
	}
}
