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

package Composestar.Core.CpsRepository2.SIInfo;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.MethodReference;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleBinding;

/**
 *A superimposed filter module
 * 
 * @author Michiel Hendriks
 */
public interface ImposedFilterModule extends RepositoryEntity
{
	/**
	 * @return The filter module binding responsible for the superimposition
	 */
	FilterModuleBinding getImposedBy();

	/**
	 * @return The condition which should evaluate to true before the filters
	 *         will be executed.
	 */
	MethodReference getCondition();

	/**
	 * @return The filter module which is superimposed.
	 */
	FilterModule getFilterModule();

	/**
	 * Set the imposed filter module. This is used to replace the generic filter
	 * module with the resolved filter module.
	 * 
	 * @param fm
	 * @throws NullPointerException Thrown when the filter module is null
	 */
	void setFilterModule(FilterModule fm) throws NullPointerException;
}
