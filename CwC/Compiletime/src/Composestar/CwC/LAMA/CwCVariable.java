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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import weavec.cmodel.declaration.ObjectDeclaration;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.EUnitType;
import Composestar.Utils.Logging.LocationProvider;

/**
 * Encapsulates a variable defined in a C file. Since the C file is considered a
 * Type the declared variables are considered fields of that type.
 * 
 * @author Michiel Hendriks
 */
public class CwCVariable extends FieldInfo implements LocationProvider
{
	private static final long serialVersionUID = 7148389289475337004L;

	protected transient ObjectDeclaration objDecl;

	protected String sourceFilename;

	protected int sourceLine;

	protected int sourceLinepos;

	public CwCVariable()
	{
		super();
	}

	public CwCVariable(ObjectDeclaration decl)
	{
		this();
		objDecl = decl;
		// sourceFilename = decl.getAST().getFirstChild().getSource();
		// sourceLine = decl.getAST().getFirstChild().getLineNum();
	}

	public ObjectDeclaration getObjectDeclaration()
	{
		return objDecl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.FieldInfo#isDeclaredHere()
	 */
	@Override
	public boolean isDeclaredHere()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.FieldInfo#isPrivate()
	 */
	@Override
	public boolean isPrivate()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.FieldInfo#isProtected()
	 */
	@Override
	public boolean isProtected()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.FieldInfo#isPublic()
	 */
	@Override
	public boolean isPublic()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection getUnitAttributes()
	{
		Set<String> res = new HashSet<String>();
		res.add("public");
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (ERelationType.PARENT_METHOD.equals(argumentName))
		{
			return new UnitResult(parent);
		}
		else if (ERelationType.CLASS.equals(argumentName) && EUnitType.CLASS.equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if (ERelationType.TYPE.equals(argumentName) && EUnitType.TYPE.equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if (ERelationType.ANNOTATIONS.equals(argumentName))
		{
			Iterator<Annotation> i = getAnnotations().iterator();
			Set<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add(i.next().getType());
			}
			return new UnitResult(res);
		}
		return new UnitResult(Collections.emptySet());
	}

	public String getFilename()
	{
		return sourceFilename;
	}

	public int getLineNumber()
	{
		return sourceLine;
	}

	public int getLinePosition()
	{
		return sourceLinepos;
	}

}
