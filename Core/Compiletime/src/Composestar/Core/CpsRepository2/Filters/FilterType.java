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

package Composestar.Core.CpsRepository2.Filters;

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * Generic interface for all filter types, which includes the primitive filter
 * types and the filter modules which can be used as filter type.
 * 
 * @author Michiel Hendriks
 */
public interface FilterType extends RepositoryEntity
{
	/**
	 * @return The full name of this filter type. For FilterModuleFilterTypes
	 *         this name would be equal to the fully qualified name of the
	 *         filter module.
	 */
	String getFilterName();

	// void isValidArgument(CanonAssignment arg) throws something;
}
