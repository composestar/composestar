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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on fields of modules to automatically set some fields
 * according to the module configuration. These annotations will also be used to
 * construct the ModuleInfo.xml files. The JavaDoc item of a field will be used
 * as the description for the configuration entry.
 * 
 * @author Michiel Hendriks
 */
/**
 * @author mhendrik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ModuleSetting
{
	/**
	 * The id of the setting, uses the field name when unset. Can be prefixed
	 * with a module name to get a setting from that module. For example the
	 * field "myVar" in the module "MyMOD" with no id value will read from the
	 * configuration entry "MyMOD.myVar" if the id value is "configItem" it will
	 * read from "MyMOD.configItem", if the id value is "OtherMOD.Config" then
	 * it will read from that entry.
	 * 
	 * @return
	 */
	String ID() default "";

	/**
	 * Human readable name for this setting. Will be used in the generation of
	 * documentation.
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * If set to true this is an advanced configuration entry that should not be
	 * exposed to the end-user unless the advanced configuration options are
	 * requested.
	 * 
	 * @return
	 */
	boolean isAdvanced() default false;

	/**
	 * The method to execute to set this field value. If this is an empty string
	 * it will simply directly write the value. Otherwise it will call this
	 * method with the configuration value, if present.
	 * 
	 * @return
	 */
	String setter() default "";

	/**
	 * If this is set to true the string value is passed to the setter rather
	 * than converting it to the type of the field
	 * 
	 * @return
	 */
	boolean setterTakesString() default false;
}
