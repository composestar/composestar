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

package Composestar.Core.TASMAN;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;

/**
 * The base for all tasks in TASMAN
 * 
 * @author Michiel Hendriks
 */
public abstract class Task
{
	protected String processId = "";

	public Task()
	{}

	public abstract void execute(Manager manager, CommonResources resources) throws ModuleException;

	protected void setProcessId(String id)
	{
		processId = id;
	}

	protected String getProcessId()
	{
		return processId;
	}
}
