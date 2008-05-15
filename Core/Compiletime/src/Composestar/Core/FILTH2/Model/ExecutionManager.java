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
 * $Id: ExcludeConstraint.java 4192 2008-05-09 09:29:31Z elmuerte $
 */
package Composestar.Core.FILTH2.Model;

/**
 * Defines the interface that keeps track of the execution and return value of
 * actions
 * 
 * @author Michiel Hendriks
 */
public interface ExecutionManager
{
	/**
	 * Get the execution result of an action
	 * 
	 * @param action
	 * @return
	 */
	ExecutionResult getResult(Action action);

	/**
	 * Set the execution result
	 * 
	 * @param action
	 * @param result
	 */
	void setResult(Action action, ExecutionResult result);

	/**
	 * Changes the executability of an action, called by cond() and skip()
	 * 
	 * @param action
	 * @param value
	 */
	void setExecutable(Action action, boolean value);

	/**
	 * Returns true if an action is executable
	 * 
	 * @param action
	 * @return
	 */
	boolean isExecutable(Action action);

}
