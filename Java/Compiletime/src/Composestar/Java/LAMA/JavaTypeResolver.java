/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Java.LAMA;

import java.util.List;

import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.TypeResolver;
import Composestar.Core.LAMA.UnitRegister;

/**
 * @author Michiel Hendriks
 */
public class JavaTypeResolver extends TypeResolver
{
	public JavaTypeResolver()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.TypeResolver#resolve(Composestar.Core.LAMA.ProgramElement,
	 *      Composestar.Core.LAMA.UnitRegister)
	 */
	@Override
	public void resolve(ProgramElement unit, UnitRegister register)
	{
		if (unit instanceof JavaType)
		{
			resolveJavaType((JavaType) unit, register);
		}
		else
		{
			super.resolve(unit, register);
		}
	}

	protected void resolveJavaType(JavaType unit, UnitRegister register)
	{
		JavaType type = (JavaType) register.getType(unit.getSuperClassString());
		if (type == null)
		{
			logger.info(String.format("Unable to find super class \"%s\" for type \"%s\"", unit.getSuperClassString(),
					unit.getFullName()));
		}
		else
		{
			unit.setSuperClass(type);
		}
		for (String iface : (List<String>) unit.getImplementedInterfaceNames())
		{
			type = (JavaType) register.getType(iface);
			if (type == null)
			{
				logger
						.info(String.format("Unable to find interface \"%s\" for type \"%s\"", iface, unit
								.getFullName()));
			}
			else
			{
				unit.addImplementedInterface(type);
			}
		}
	}
}
