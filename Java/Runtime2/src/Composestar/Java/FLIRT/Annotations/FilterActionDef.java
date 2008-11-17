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

package Composestar.Java.FLIRT.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import Composestar.Java.FLIRT.Interpreter.MessageFlow;

/**
 * Filter action definition. These will be harvested for information
 * 
 * @author Michiel Hendriks
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterActionDef
{
	/**
	 * @return The name of the filter action
	 */
	String name();

	/**
	 * @return The change to the message flow this action will absolutely make,
	 *         conditional changes should use continue;
	 */
	MessageFlow messageChangeBehavior() default MessageFlow.CONTINUE;

	/**
	 * @return The resource operation sequence
	 */
	String resourceOperations() default "";

	/**
	 * @return Filter argument names
	 */
	String[] arguments() default {};

	/**
	 * @return The allowed value types for the arguments (optional)
	 */
	CpsVariableType[] argumentTypes() default {};

	/**
	 * @return True if the argument is required (optional, defaults to all
	 *         false)
	 */
	boolean[] requiredArgument() default {};
}
