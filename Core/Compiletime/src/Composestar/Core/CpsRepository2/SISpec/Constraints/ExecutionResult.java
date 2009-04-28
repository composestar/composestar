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
	 * <table border="1">
	 * <tr>
	 * <th>hard AND</th>
	 * <th>True</th>
	 * <th>False</th>
	 * <th>Unset</th>
	 * </tr>
	 * <tr>
	 * <th>True</th>
	 * <td>True</td>
	 * <td>False</td>
	 * <td>False</td>
	 * </tr>
	 * <tr>
	 * <th>False</th>
	 * <td>False</td>
	 * <td>False</td>
	 * <td>False</td>
	 * </tr>
	 * <tr>
	 * <th>Unset</th>
	 * <td>False</td>
	 * <td>False</td>
	 * <td>Unset</td>
	 * </tr>
	 * </table>
	 * 
	 * @param rhs
	 * @return
	 */
	public ExecutionResult hardAnd(ExecutionResult rhs)
	{
		switch (this)
		{
			case NOT_EXECUTED:
			case UNSET:
				if (rhs == NOT_EXECUTED || rhs == UNSET)
				{
					return rhs;
				}
				return FALSE;
			case FALSE:
				return FALSE;
			case TRUE:
				if (rhs == TRUE)
				{
					return TRUE;
				}
				return FALSE;
			default:
				// not possible
				return this;
		}
	}

	/**
	 * The result of (this && rhs) *
	 * <table border="1">
	 * <tr>
	 * <th>soft AND</th>
	 * <th>True</th>
	 * <th>False</th>
	 * <th>Unset</th>
	 * </tr>
	 * <tr>
	 * <th>True</th>
	 * <td>True</td>
	 * <td>False</td>
	 * <td>True</td>
	 * </tr>
	 * <tr>
	 * <th>False</th>
	 * <td>False</td>
	 * <td>False</td>
	 * <td>False</td>
	 * </tr>
	 * <tr>
	 * <th>Unset</th>
	 * <td>True</td>
	 * <td>False</td>
	 * <td>Unset</td>
	 * </tr>
	 * </table>
	 * 
	 * @param rhs
	 * @return
	 */
	public ExecutionResult softAnd(ExecutionResult rhs)
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
	 * <table border="1">
	 * <tr>
	 * <th>sort OR</th>
	 * <th>True</th>
	 * <th>False</th>
	 * <th>Unset</th>
	 * </tr>
	 * <tr>
	 * <th>True</th>
	 * <td>True</td>
	 * <td>True</td>
	 * <td>True</td>
	 * </tr>
	 * <tr>
	 * <th>False</th>
	 * <td>True</td>
	 * <td>False</td>
	 * <td>False</td>
	 * </tr>
	 * <tr>
	 * <th>Unset</th>
	 * <td>True</td>
	 * <td>False</td>
	 * <td>Unset</td>
	 * </tr>
	 * </table>
	 * 
	 * @param rhs
	 * @return
	 */
	public ExecutionResult softOr(ExecutionResult rhs)
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

	/**
	 * The result of !this
	 * <table border="1">
	 * <tr>
	 * <th>Not</th>
	 * <th>True</th>
	 * <th>False</th>
	 * <th>Unset</th>
	 * </tr>
	 * <tr>
	 * <th></th>
	 * <td>False</td>
	 * <td>True</td>
	 * <td>Unset</td>
	 * </tr>
	 * </table>
	 * 
	 * @param rhs
	 * @return
	 */
	public ExecutionResult not()
	{
		switch (this)
		{
			case TRUE:
				return FALSE;
			case FALSE:
				return TRUE;
			case NOT_EXECUTED:
			case UNSET:
			default:
				return this;
		}
	}
}
