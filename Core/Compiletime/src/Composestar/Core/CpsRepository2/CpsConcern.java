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

import java.util.Collection;

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
	 *         modules exist in this concern an empty set is returned. The
	 *         returned collection is read only.
	 */
	Collection<FilterModule> getFilterModules();

	/**
	 * Add a new filter module to this concern. The names of filter modules must
	 * be unique, if there is already a filter module with the same name the new
	 * filter module will not be added and false will be returned. When the
	 * filter module is added setOwner(this) will be called on the filter module
	 * instance.
	 * 
	 * @param fm The filter module to add.
	 * @return True when the filter module was added. False is returned when
	 *         there was already a filter module with that name registered.
	 * @throws NullPointerException Thrown when the provided filter module is
	 *             null.
	 */
	boolean addFilterModule(FilterModule fm) throws NullPointerException;

	/**
	 * Retrieves a filter module with a given name.
	 * 
	 * @param fmName The name of the filter module, this is not a fully
	 *            qualified name.
	 * @return The filter module instance or null when no filter module with
	 *         that name could be found.
	 */
	FilterModule getFilterModule(String fmName);

	/**
	 * Remove a filter module from this concern
	 * 
	 * @param fm The filter module to remove
	 * @return The filter module that was removed (which should be the same
	 *         instance as the provided filter module), or null if nothing was
	 *         removed.
	 * @throws NullPointerException Thrown when the provided filter module is
	 *             null.
	 * @see #removeFilterModule(String)
	 */
	FilterModule removeFilterModule(FilterModule fm) throws NullPointerException;

	/**
	 * Remove a filter module with a given name.
	 * 
	 * @param fmName The name of the filter module to remove.
	 * @return The filter module that was removed or null when no filter module
	 *         with that name was found.
	 * @see #getFilterModule(String)
	 * @see #removeFilterModule(FilterModule)
	 */
	FilterModule removeFilterModule(String fmName);

	/**
	 * @return The superimposition information defined in this concern. When the
	 *         concern does not have any superimposition null will be returned.
	 * @see #setSuperImposition(SuperImposition)
	 */
	SuperImposition getSuperImposition();

	/**
	 * Set the superimposition for this concern. Previously associated
	 * superimposition can be removed by passing null as the argument. This
	 * method will call setOwner(this) on the provided superimposition instance.
	 * 
	 * @param si the superimposition instance to set
	 * @throws IllegalStateException when the concern already has a
	 *             superimposition set and the new value is not null. To
	 *             overwrite the previous value it first has to be set to null.
	 *             This exception is not thrown when the current value and new
	 *             value are the same.
	 * @see #getSuperImposition()
	 */
	void setSuperImposition(SuperImposition si) throws IllegalStateException;
}
