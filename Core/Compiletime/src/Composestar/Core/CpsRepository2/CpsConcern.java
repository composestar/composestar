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

package Composestar.Core.CpsRepository2;

import java.util.Set;

import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.SuperImposition.SuperImposition;

/**
 * A concern which originates from a Compose* concern definition (.cps file).
 * 
 * @author Michiel Hendriks
 */
public interface CpsConcern extends Concern
{
	/**
	 * @return All filter modules registered in this CpsConcern. If no filter
	 *         modules exist in this concern an empty set is returned.
	 */
	Set<FilterModule> getFilterModules();

	/**
	 * @return The superimposition information defined in this concern. When the
	 *         concern does not have any superimposition null will be returned.
	 */
	SuperImposition getSuperImposition();

	/**
	 * Set the superimposition for this concern. Previously associated
	 * superimposition can be removed by passing null as the argument.
	 * 
	 * @param si the superimposition instance to set
	 * @throws IllegalArgumentException when the concern already has a
	 *             superimposition set and the new value is not null. To
	 *             overwrite the previous value it first has to be set to null.
	 */
	// FIXME IllegalArgumentException is incorrect
	void setSuperImposition(SuperImposition si) throws IllegalArgumentException;
}
