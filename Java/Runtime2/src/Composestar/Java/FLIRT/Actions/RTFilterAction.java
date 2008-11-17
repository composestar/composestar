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

import Composestar.Java.FLIRT.Env.RTMessage;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * A filter action
 * 
 * @author Michiel Hendriks
 */
public abstract class RTFilterAction
{
	/**
	 * Called when this action is executed
	 * 
	 * @param matchedMessage The message that was matched, this is not the
	 *            current message, and it does not have the substitutions
	 *            applied. Changing this message has no effect.
	 * @param context The current execution context.
	 * @see FilterExecutionContext#getMessage()
	 */
	public abstract void execute(RTMessage matchedMessage, FilterExecutionContext context);
}
