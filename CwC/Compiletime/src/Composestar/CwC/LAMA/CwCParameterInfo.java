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

package Composestar.CwC.LAMA;

import java.util.Collection;
import java.util.Collections;

import weavec.cmodel.declaration.ObjectDeclaration;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.UnitResult;

/**
 * @author Michiel Hendriks
 */
public class CwCParameterInfo extends ParameterInfo
{
	private static final long serialVersionUID = 8367235262502160776L;

	protected ObjectDeclaration objDecl;

	public CwCParameterInfo(ObjectDeclaration decl)
	{
		super();
		objDecl = decl;
	}

	public ObjectDeclaration getObjectDeclaration()
	{
		return objDecl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection getUnitAttributes()
	{
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if ("ParentMethod".equals(argumentName))
		{
			return new UnitResult(parent);
		}
		else if ("Class".equals(argumentName) && "Class".equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		else if ("Type".equals(argumentName) && "Type".equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		return null;
	}
}
