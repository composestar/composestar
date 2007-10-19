/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.Resources;

import java.io.Serializable;

/**
 * Generic interface for the resource managers of the Compose* modules. All non
 * runtime specific module results should me stored in a resource manager of a
 * module. Each module should implement their own resource manager with specific
 * accessor methods to receive the requested results. Module that require
 * results of a specific module should retrieve this information through the
 * resource manager. All resource managers should be stored in the
 * CommonResources instance passed to each module. ResourceManagers are
 * serializable, so data contained in the resource managers should also be
 * serializable, or stored in a transient field.
 * 
 * @author Michiel Hendriks
 */
public interface ModuleResourceManager extends Serializable
{
	/**
	 * Return the name of the module associated with this resource manager
	 * 
	 * @return
	 */
	String getModuleName();
}
