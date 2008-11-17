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

package Composestar.Java.FLIRT.Actions;

import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.FilterActionNames;

/**
 * @author Michiel Hendriks
 */
public final class RTFilterActionFactory
{
	/**
	 * Create the runtime filter action.
	 * 
	 * @param action
	 * @return The action to execute, or null when no action should be executed
	 */
	public static final RTFilterAction createAction(FilterAction action)
	{
		if (FilterActionNames.DISPATCH_ACTION.equals(action.getName()))
		{
			return new DispatchAction();
		}
		else if (FilterActionNames.SEND_ACTION.equals(action.getName()))
		{
			return new SendAction();
		}
		else if (FilterActionNames.CONTINUE_ACTION.equals(action.getName()))
		{
			return null;
		}
		else if (FilterActionNames.ERROR_ACTION.equals(action.getName()))
		{
			return new ErrorAction();
		}
		else if (FilterActionNames.META_ACTION.equals(action.getName()))
		{

		}
		else if (FilterActionNames.SUBSTITUTION_ACTION.equals(action.getName()))
		{
			return null;
		}
		else if (FilterActionNames.ADVICE_ACTION.equals(action.getName()))
		{

		}
		// TODO: custom filter type
		return null;
	}
}
