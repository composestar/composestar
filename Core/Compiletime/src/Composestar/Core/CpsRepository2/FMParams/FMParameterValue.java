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

package Composestar.Core.CpsRepository2.FMParams;

import java.util.Collection;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * A filter module parameter value. Within the language there is a distinction
 * between a single value and value list. Both are represented by the same
 * interface because at the time of the declaration of the value it is unclear
 * which of the two options is desired by the filter module. During
 * instantiation of the parameterized filter module the value type will be
 * validated. This is also done because a selector can be used as parameter
 * value, which can resolve to multiple program elements or just one. A list can
 * contain 0 to many values and a single parameter requires 1 value.
 * 
 * @author Michiel Hendriks
 */
public interface FMParameterValue extends RepositoryEntity
{
	/**
	 * @return The values assigned to this filter module parameter.
	 */
	Collection<CpsVariable> getValues();
}
