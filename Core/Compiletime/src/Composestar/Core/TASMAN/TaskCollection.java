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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A collection of tasks to be executed in some way.
 * 
 * @author Michiel Hendriks
 */
public abstract class TaskCollection extends Task
{
	protected List<Task> tasks;

	public TaskCollection()
	{
		super();
		tasks = new ArrayList<Task>();
	}

	/**
	 * Add a new task to this collection.
	 * 
	 * @param newtask
	 */
	public void addTask(Task newtask)
	{
		tasks.add(newtask);
	}

	/**
	 * @return the tasks in this collection
	 */
	public List<Task> getTasks()
	{
		return Collections.unmodifiableList(tasks);
	}
}
