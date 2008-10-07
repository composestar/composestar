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

import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * The applied superimposition (previously called SIinfo).
 * 
 * @author Michiel Hendriks
 */
public interface Superimposed extends RepositoryEntity
{
	/**
	 * @return The list of imposed filter modules (unordered).
	 */
	List<ImposedFilterModule> getFilterModules();

	/**
	 * Add a filter modules which should be superimposed. After added
	 * setOwner(this) is called on the imposed filter module.
	 * 
	 * @param ifm The filter module to add
	 * @throws NullPointerException Thrown when the imposed filter module is
	 *             null.
	 */
	void addFilterModule(ImposedFilterModule ifm) throws NullPointerException;

	/**
	 * Add a filter module order. The first order added will automatically
	 * because the selected filter module order.
	 * 
	 * @param order The order to add
	 * @throws NullPointerException Thrown when the order is null
	 * @throws IllegalArgumentException Thrown when the order does not have the
	 *             same content as the filter module list
	 */
	void addFilterModuleOrder(List<ImposedFilterModule> order) throws NullPointerException, IllegalArgumentException;

	/**
	 * @return The selected filter module order. Returns null when no order has
	 *         been set. The returned list can not be modified.
	 */
	List<ImposedFilterModule> getFilterModuleOrder();

	/**
	 * Set the current filter module order. If this order is not in the
	 * currently registered list it will be added.
	 * 
	 * @param order The active filter module order to set.
	 * @throws NullPointerException Thrown when the order is null
	 * @throws IllegalArgumentException Thrown when the order does not have the
	 *             same content as the filter module list
	 */
	void setFilterModuleOrder(List<ImposedFilterModule> order) throws NullPointerException, IllegalArgumentException;

	/**
	 * @return All filter module orders registered. This list does not have to
	 *         be complete (depends on FILTH). When no orders where registered
	 *         an empty collection is returned.
	 */
	Collection<List<ImposedFilterModule>> getAllOrders();
}
