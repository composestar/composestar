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

import Composestar.Java.FLIRT.Actions.ContinueAction;
import Composestar.Java.FLIRT.Actions.RTFilterAction;

/**
 * Annotation needed for custom runtime filters
 * 
 * @author Michiel Hendriks
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterTypeDef
{
	/**
	 * The name of the filter
	 */
	String name();

	/**
	 * The filter action when the message is accepted
	 */
	Class<? extends RTFilterAction> acceptCall() default ContinueAction.class;

	/**
	 * The return filter action when the message is accepted
	 */
	Class<? extends RTFilterAction> acceptReturn() default ContinueAction.class;

	/**
	 * The filter action when the message is rejected
	 */
	Class<? extends RTFilterAction> rejectCall() default ContinueAction.class;

	/**
	 * The return filter action when the message is rejected
	 */
	Class<? extends RTFilterAction> rejectReturn() default ContinueAction.class;
}
