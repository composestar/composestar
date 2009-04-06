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
import java.util.Set;

import weavec.cmodel.declaration.TypeDeclaration;
import weavec.cmodel.type.CType;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.EUnitType;
import Composestar.Utils.Logging.LocationProvider;

/**
 * Encapsulates an actual C type (unlike CwCFile). However, these instances are
 * not considered primitive concerns.
 * 
 * @author Michiel Hendriks
 */
public class CwCType extends Type implements LocationProvider
{
	private static final long serialVersionUID = -447789582637518491L;

	protected transient TypeDeclaration typeDecl;

	protected transient CType cType;

	protected Set<MethodInfo> methodReturnTypes;

	protected Set<ParameterInfo> parameterTypes;

	protected Set<FieldInfo> fieldTypes;

	public CwCType()
	{
		super();
		methodReturnTypes = new HashSet<MethodInfo>();
		parameterTypes = new HashSet<ParameterInfo>();
		fieldTypes = new HashSet<FieldInfo>();
	}

	public CwCType(TypeDeclaration decl)
	{
		this(decl.getType());
		typeDecl = decl;
	}

	public CwCType(CType ct)
	{
		this();
		cType = ct;
	}

	/**
	 * Can be null in case the type is not declared (but derived, like a point
	 * of a type or an array of a type).
	 * 
	 * @return
	 */
	public TypeDeclaration getTypeDeclaration()
	{
		return typeDecl;
	}

	public CType getCType()
	{
		return cType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.Type#addChildType(Composestar.Core.LAMA.ProgramElement
	 * )
	 */
	@Override
	public void addChildType(ProgramElement childType)
	{
	// TODO derived types?
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.Type#addFieldType(Composestar.Core.LAMA.ProgramElement
	 * )
	 */
	@Override
	public void addFieldType(ProgramElement fieldType)
	{
		fieldTypes.add((FieldInfo) fieldType);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.LAMA.Type#addImplementedBy(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void addImplementedBy(ProgramElement class1)
	{
		throw new IllegalArgumentException("No such thing as interfaces");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.Type#addMethodReturnType(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void addMethodReturnType(ProgramElement returnType)
	{
		methodReturnTypes.add((MethodInfo) returnType);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.LAMA.Type#addParameterType(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void addParameterType(ProgramElement paramType)
	{
		parameterTypes.add((ParameterInfo) paramType);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.Type#namespace()
	 */
	@Override
	public String namespace()
	{
		// TODO null for namespace? what about declaring file?
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.LAMA.Type#setParentNamespace(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void setParentNamespace(ProgramElement parentNS)
	{
	// TODO see #namespace()
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection getUnitAttributes()
	{
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (ERelationType.METHOD_RETURN_CLASS.equals(argumentName))
		{
			return new UnitResult(methodReturnTypes);
		}
		else if (ERelationType.PARAMETER_CLASS.equals(argumentName))
		{
			return new UnitResult(parameterTypes);
		}
		else if (ERelationType.FIELD_CLASS.equals(argumentName))
		{
			return new UnitResult(fieldTypes);
		}
		else if (ERelationType.IMPLEMENTS.equals(argumentName))
		{
			return new UnitResult(new HashSet<Type>());
		}
		Set<ProgramElement> pe = Collections.emptySet();
		return new UnitResult(pe);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	@Override
	public String getUnitType()
	{
		return EUnitType.TYPE.toString();
	}

	public String getFilename()
	{
		return null;
	}

	public int getLineNumber()
	{
		return 0;
	}

	public int getLinePosition()
	{
		return 0;
	}

}
