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

import java.io.Serializable;
import java.util.Collection;

/**
 * A filter module parameter value. Within the language there is a distinction
 * between a single value and value list. Both are represented by the same
 * interface because at the time of the declaration of the value it is unclear
 * which of the two options is desired by the filter module. During
 * instantiation of the parameterized filter module the the value type will be
 * validated. A list can contain 0 to many values and a single parameter
 * requires 1 value. <br/> This interface is defined as a generic because
 * parameter can contain LAMA program elements or literals. It all depends on
 * where and how they are used. The data will be validated when the filter
 * modules are instantiated. See the ARM for more information on how the
 * parameters are used.
 * 
 * @author Michiel Hendriks
 */
public interface FMParameterValue<T> extends Serializable
{
	/**
	 * @return The values assigned to this filter module parameter.
	 */
	Collection<T> getValues();
}
