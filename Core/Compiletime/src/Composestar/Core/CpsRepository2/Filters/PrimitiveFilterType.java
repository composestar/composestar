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


/**
 * Defines the filter type.
 * 
 * @author Michiel Hendriks
 */
// FIXME reject action?
public interface PrimitiveFilterType extends FilterType
{
	/**
	 * @return The name of this filter type.
	 */
	String getName();

	/**
	 * @return The filter action for the accept-call
	 */
	FilterAction getAcceptCallAction();

	/**
	 * @return The filter action for the reject-call
	 */
	FilterAction getRejectCallAction();

	/**
	 * @return The filter action for the accept-return
	 */
	FilterAction getAcceptReturnAction();

	/**
	 * @return The filter action for the reject-return
	 */
	FilterAction getRejectReturnAction();
}
