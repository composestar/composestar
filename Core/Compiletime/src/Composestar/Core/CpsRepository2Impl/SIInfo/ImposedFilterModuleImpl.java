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

import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class ImposedFilterModuleImpl extends AbstractRepositoryEntity implements ImposedFilterModule
{
	private static final long serialVersionUID = 6810664520502514731L;

	/**
	 * The imposing filter module binding
	 */
	protected FilterModuleBinding imposedBy;

	/**
	 * The optional condition
	 */
	protected MethodReference methodReference;

	/**
	 * The superimposed filter module
	 */
	protected FilterModule filterModule;

	/**
	 * Create a new imposed filter module instance
	 * 
	 * @param imposer
	 * @throws NullPointerException Thrown when the filter module binding is
	 *             null
	 */
	public ImposedFilterModuleImpl(FilterModuleBinding imposer) throws NullPointerException
	{
		super();
		imposedBy = imposer;
		filterModule = imposedBy.getFilterModuleReference().getReference();
		if (imposedBy.getCondition() != null)
		{
			methodReference = imposedBy.getCondition().getMethodReference();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#getCondition()
	 */
	public MethodReference getCondition()
	{
		return methodReference;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#getFilterModule
	 * ()
	 */
	public FilterModule getFilterModule()
	{
		return filterModule;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#setFilterModule
	 * (Composestar.Core.CpsRepository2.FilterModules.FilterModule)
	 */
	public void setFilterModule(FilterModule fm) throws NullPointerException
	{
		if (fm == null)
		{
			throw new NullPointerException("Filter module can not be null");
		}
		filterModule = fm;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule#getImposedBy()
	 */
	public FilterModuleBinding getImposedBy()
	{
		return imposedBy;
	}

}
