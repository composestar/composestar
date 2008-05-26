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

package Composestar.Core.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to provide meta data for Compose* modules. In the future this
 * annotation will be used to automatically generate the moduleinfo.xml files.
 * 
 * @author Michiel Hendriks
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ComposestarModule
{
	/**
	 * Defines the importance of this module. There are three levels of
	 * importance, from high to low: Required, Validation, Advising. Using a
	 * configuration option a collection of modules could be disabled.
	 */
	public enum Importance
	{
		/**
		 * This module is absolutely vital for the compilation process.
		 */
		REQUIRED,
		/**
		 * This module will validate various parts during the compilation
		 * process. Disabling this module could result in less instructive
		 * errors at a later stage during compilation.
		 */
		VALIDATION,
		/**
		 * This module will analyze the resulting program for possible problems.
		 * Disabling this module should have no influence in in the resulting
		 * program (except that additional safety checks might not be included).
		 */
		ADVISING;
	}

	/**
	 * Depends on the proper execution of all previous modules. In case of
	 * incremental compilation this means that this module will always execute.
	 */
	public static final String DEPEND_ALL = "*";

	/**
	 * It depends on the module that was executed before this module.
	 */
	public static final String DEPEND_PREVIOUS = "<";

	/**
	 * The ID of this module
	 * 
	 * @return
	 */
	String ID();

	/**
	 * IDs of modules this module depends on. Will be used for conditional
	 * execution of this module depending on the result of the depending
	 * modules.
	 * 
	 * @return
	 * @see #DEPEND_ALL
	 * @see #DEPEND_PREVIOUS
	 */
	String[] dependsOn() default {};

	/**
	 * Defines the role of this module within the whole Compose* compiler chain.
	 * It defaults to the Required level.
	 * 
	 * @return
	 */
	Importance importancex() default Importance.REQUIRED;

}
