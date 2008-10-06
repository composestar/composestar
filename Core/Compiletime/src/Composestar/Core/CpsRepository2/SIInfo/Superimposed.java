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
}
