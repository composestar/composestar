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

import weavec.cmodel.declaration.AnnotationDeclaration;
import weavec.cmodel.type.AnnotationType;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.EUnitType;

/**
 * Encapsulates the annotation type, it's not a CType therefor CwCType can not
 * be used for it.
 * 
 * @author Michiel Hendriks
 */
public class CwCAnnotationType extends Type
{
	private static final long serialVersionUID = -439148456392869374L;

	protected transient AnnotationDeclaration annotDecl;

	protected transient AnnotationType annotType;

	public CwCAnnotationType()
	{
		super();
	}

	public CwCAnnotationType(AnnotationDeclaration decl)
	{
		this(decl.getType());
		annotDecl = decl;
		setName(decl.getName());
	}

	protected CwCAnnotationType(AnnotationType at)
	{
		this();
		annotType = at;
	}

	public AnnotationDeclaration getAnnotationDeclaration()
	{
		return annotDecl;
	}

	public AnnotationType getAnnotationType()
	{
		return annotType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addChildType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addChildType(ProgramElement childType)
	{
	// not possible
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addFieldType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addFieldType(ProgramElement fieldType)
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addImplementedBy(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addImplementedBy(ProgramElement class1)
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addMethodReturnType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addMethodReturnType(ProgramElement returnType)
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addParameterType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addParameterType(ProgramElement paramType)
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#namespace()
	 */
	@Override
	public String namespace()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#setParentNamespace(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void setParentNamespace(ProgramElement parentNS)
	{}

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
		return new UnitResult(Collections.emptySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	@Override
	public String getUnitType()
	{
		return EUnitType.TYPE.toString();
	}

}
