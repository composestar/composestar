/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007-2008 University of Twente.
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

package Composestar.Core.LAMA;

import Composestar.Utils.Logging.CPSLogger;

/**
 * Resolve types
 * 
 * @author Michiel Hendriks
 */
public abstract class TypeResolver
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("LAMA");

	/**
	 * Will be called by UnitRegister to resolve a type;
	 * 
	 * @param unit
	 * @param register
	 */
	public void resolve(ProgramElement unit, UnitRegister register)
	{
		if (unit instanceof FieldInfo)
		{
			resolveFieldInfo((FieldInfo) unit, register);
		}
		else if (unit instanceof MethodInfo)
		{
			resolveMethodInfo((MethodInfo) unit, register);
		}
		else if (unit instanceof ParameterInfo)
		{
			resolveParameterInfo((ParameterInfo) unit, register);
		}
	}

	protected void resolveFieldInfo(FieldInfo unit, UnitRegister register)
	{
		Type type = register.getType(unit.getFieldTypeString());
		if (type == null)
		{
			logger.info(String.format("Unable to find type \"%s\" for field \"%s\"", unit.getFieldTypeString(), unit
					.getName()));
		}
		else
		{
			unit.setFieldType(type);
		}
	}

	protected void resolveMethodInfo(MethodInfo unit, UnitRegister register)
	{
		Type type = register.getType(unit.getReturnTypeString());
		if (type == null)
		{
			logger.info(String.format("Unable to find return type \"%s\" for method \"%s\"",
					unit.getReturnTypeString(), unit.getName()));
		}
		else
		{
			unit.setReturnType(type);
		}
	}

	protected void resolveParameterInfo(ParameterInfo unit, UnitRegister register)
	{
		Type type = register.getType(unit.getParameterTypeString());
		if (type == null)
		{
			logger.info(String.format("Unable to find parameter type \"%s\" for parameter \"%s\"", unit
					.getParameterTypeString(), unit.getName()));
		}
		else
		{
			unit.setParameterType(type);
		}
	}
}
