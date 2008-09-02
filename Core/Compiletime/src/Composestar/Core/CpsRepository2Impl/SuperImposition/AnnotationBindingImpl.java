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

package Composestar.Core.CpsRepository2Impl.SuperImposition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Composestar.Core.CpsRepository2.References.TypeReference;
import Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Basic implementation of the AnnotationBinding interface
 * 
 * @author "Michiel Hendriks"
 */
public class AnnotationBindingImpl extends AbstractRepositoryEntity implements AnnotationBinding
{
	private static final long serialVersionUID = 7760773141258905514L;

	/**
	 * The used selector
	 */
	public Selector selector;

	/**
	 * All annotations bound to this selector's value
	 */
	public Set<TypeReference> annotations;

	/**
	 * Create a new annotation binding
	 */
	public AnnotationBindingImpl()
	{
		annotations = new HashSet<TypeReference>();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.AnnotationBinding#
	 * addAnnotation(Composestar.Core.CpsRepository2.References.TypeReference)
	 */
	public void addAnnotation(TypeReference annotationType) throws NullPointerException
	{
		if (annotationType == null)
		{
			throw new NullPointerException();
		}
		annotations.add(annotationType);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.AnnotationBinding#
	 * getAnnotations()
	 */
	public Collection<TypeReference> getAnnotations()
	{
		return Collections.unmodifiableCollection(annotations);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding#getSelector
	 * ()
	 */
	public Selector getSelector()
	{
		return selector;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.AnnotationBinding#
	 * removeAnnotation
	 * (Composestar.Core.CpsRepository2.References.TypeReference)
	 */
	public TypeReference removeAnnotation(TypeReference annotationType) throws NullPointerException
	{
		if (annotationType == null)
		{
			throw new NullPointerException();
		}
		if (annotations.remove(annotationType))
		{
			return annotationType;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.AnnotationBinding#
	 * removeAnnotation(java.lang.String)
	 */
	public TypeReference removeAnnotation(String referenceId)
	{
		Iterator<TypeReference> it = annotations.iterator();
		while (it.hasNext())
		{
			TypeReference annot = it.next();
			if (annot.getReferenceId().equals(referenceId))
			{
				it.remove();
				return annot;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SuperImposition.AnnotationBinding#setSelector
	 * (Composestar.Core.CpsRepository2.SuperImposition.Selector)
	 */
	public void setSelector(Selector sel) throws NullPointerException
	{
		if (sel == null)
		{
			throw new NullPointerException();
		}
		selector = sel;
	}

}
