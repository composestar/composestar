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

package Composestar.Core.CpsRepository2.SISpec;

import Composestar.Core.CpsRepository2.RepositoryEntity;

/**
 * Interface used for all values of a filter module constraint. This interface
 * is used to group the filter module reference and superimposition condition.
 * 
 * @author Michiel Hendriks
 */
public interface ConstraintValue extends RepositoryEntity
{
	/**
	 * @return The the value as string. This would be in most cases the fully
	 *         qualified names.
	 */
	String getStringValue();
}
