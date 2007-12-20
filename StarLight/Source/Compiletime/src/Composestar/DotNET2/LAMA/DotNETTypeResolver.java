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

package Composestar.DotNET2.LAMA;

import java.util.List;

import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.TypeResolver;
import Composestar.Core.LAMA.UnitRegister;

/**
 * @author Michiel Hendriks
 */
public class DotNETTypeResolver extends TypeResolver
{
	public DotNETTypeResolver()
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
		if (unit instanceof DotNETType)
		{
			resolveDotNETType((DotNETType) unit, register);
		}
		else
		{
			super.resolve(unit, register);
		}
	}

	protected void resolveDotNETType(DotNETType unit, UnitRegister register)
	{
		if (unit.getBaseTypeString() != null)
		{
			DotNETType type = (DotNETType) register.getType(unit.getBaseTypeString());
			if (type == null)
			{
				logger.info(String.format("Unable to find base type \"%s\" for type \"%s\"", unit.getBaseTypeString(),
						unit.getFullName()));
			}
			else
			{
				unit.setBaseType(type);
			}
		}
		for (String iface : (List<String>) unit.getImplementedInterfaceNames())
		{
			DotNETType type = (DotNETType) register.getType(iface);
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
