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

import weavec.cmodel.declaration.Annotation;
import weavec.cmodel.type.AnnotationType;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.ERelationType;
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

	protected transient Annotation annot;

	protected transient AnnotationType annotType;

	public CwCAnnotationType()
	{
		super();
	}

	public CwCAnnotationType(Annotation annotatation)
	{
		this(annotatation.getType());
		annot = annotatation;
		setName(annot.getName());
		// TODO maybe use module.name ??
		setFullName(annot.getName());
	}

	protected CwCAnnotationType(AnnotationType at)
	{
		this();
		annotType = at;
	}

	public Annotation getAnnotationDeclaration()
	{
		return annot;
	}

	public AnnotationType getAnnotationType()
	{
		return annotType;
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
	// not possible
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.Type#addFieldType(Composestar.Core.LAMA.ProgramElement
	 * )
	 */
	@Override
	public void addFieldType(ProgramElement fieldType)
	{}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.LAMA.Type#addImplementedBy(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void addImplementedBy(ProgramElement class1)
	{}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.Type#addMethodReturnType(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void addMethodReturnType(ProgramElement returnType)
	{}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.LAMA.Type#addParameterType(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void addParameterType(ProgramElement paramType)
	{}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.Type#namespace()
	 */
	@Override
	public String namespace()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.LAMA.Type#setParentNamespace(Composestar.Core.LAMA.
	 * ProgramElement)
	 */
	@Override
	public void setParentNamespace(ProgramElement parentNS)
	{}

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
		Set<Type> resClasses = new HashSet<Type>();
		Set<MethodInfo> resMethods = new HashSet<MethodInfo>();
		Set<FieldInfo> resFields = new HashSet<FieldInfo>();
		Set<ParameterInfo> resParameters = new HashSet<ParameterInfo>();

		Iterator<Composestar.Core.LAMA.Annotation> i = getAnnotationInstances().iterator();
		while (i.hasNext())
		{
			ProgramElement unit = i.next().getTarget();
			if (unit instanceof CwCFile)
			{
				resClasses.add((Type) unit);
			}
			else if (unit instanceof MethodInfo)
			{
				resMethods.add((MethodInfo) unit);
			}
			else if (unit instanceof FieldInfo)
			{
				resFields.add((FieldInfo) unit);
			}
			else if (unit instanceof ParameterInfo)
			{
				resParameters.add((ParameterInfo) unit);
			}
		}

		if (ERelationType.ATTACHED_CLASSES.equals(argumentName))
		{
			return new UnitResult(resClasses);
		}
		else if (ERelationType.ATTACHED_METHODS.equals(argumentName))
		{
			return new UnitResult(resMethods);
		}
		else if (ERelationType.ATTACHED_FIELDS.equals(argumentName))
		{
			return new UnitResult(resFields);
		}
		else if (ERelationType.ATTACHED_PARAMETERS.equals(argumentName))
		{
			return new UnitResult(resParameters);
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
		return EUnitType.ANNOTATION.toString();
	}

}
