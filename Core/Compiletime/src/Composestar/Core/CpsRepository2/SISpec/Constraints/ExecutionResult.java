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

package Composestar.Core.CpsRepository2.SISpec.Constraints;

/**
 * Defines the execution result of an action
 * 
 * @author Michiel Hendriks
 */
public enum ExecutionResult
{
	/**
	 * Result is set to true
	 */
	TRUE,
	/**
	 * Result is set to false
	 */
	FALSE,
	/**
	 * No result value has been set
	 */
	UNSET,
	/**
	 * This action has not be executed yet
	 */
	NOT_EXECUTED;

	/**
	 * Convert a boolean value to an execution result.
	 * 
	 * @param val
	 * @return
	 */
	public static ExecutionResult fromBoolean(boolean val)
	{
		if (val) return TRUE;
		return FALSE;
	}

	/**
	 * The result of (this && rhs)
	 * 
	 * @param rhs
	 * @return
	 */
	public ExecutionResult and(ExecutionResult rhs)
	{
		switch (this)
		{
			case NOT_EXECUTED:
			case UNSET:
				return rhs;
			case FALSE:
				return FALSE;
			case TRUE:
				if (rhs == UNSET || rhs == NOT_EXECUTED)
				{
					return this;
				}
				return rhs;
			default:
				// not possible
				return this;
		}
	}

	/**
	 * The result of (this || rhs)
	 * 
	 * @param rhs
	 * @return
	 */
	public ExecutionResult or(ExecutionResult rhs)
	{
		switch (this)
		{
			case NOT_EXECUTED:
			case UNSET:
				return rhs;
			case TRUE:
				return TRUE;
			case FALSE:
				if (rhs == UNSET || rhs == NOT_EXECUTED)
				{
					return this;
				}
				return rhs;
			default:
				// not possible
				return this;
		}
	}
}
