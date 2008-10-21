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
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.DefaultNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arjan de Roo
 * @deprecated this construction is completely and utterly useless in the newer
 *             groove
 */
@Deprecated
public class AnnotatedNode extends DefaultNode
{
	private static final long serialVersionUID = -4652113596047195818L;

	/**
	 * Dictionary containing the annotations.
	 */
	private Map<String, Object> annotations;

	public AnnotatedNode()
	{
		annotations = new HashMap<String, Object>();
	}

	/**
	 * This method returns the annotation corresponding with the given
	 * annotationId.
	 * 
	 * @param annotationId The id of the annotation.
	 * @return The annotation corresponding with the annotationId, or
	 *         <code>null</code> when no annotation corresponds with the id.
	 * @exception NullPointerException if the annotationId is <code>null</code>
	 */
	public Object getAnnotation(String annotationId)
	{
		return annotations.get(annotationId);
	}

	/**
	 * This method adds an annotation to this node.
	 * 
	 * @param annotationId The id of the annotation.
	 * @param annotation The annotation. If this is <code>null</code> this
	 *            method has the same behaviour as
	 *            <code>removeAnnotation( annotationId )</code>
	 * @exception NullPointerException if annotationId is <code>null</code>
	 * @see AnnotatedNode.removeAnnotation( String )
	 */
	public void addAnnotation(String annotationId, Object annotation)
	{
		if (annotation == null)
		{
			removeAnnotation(annotationId);
		}
		else
		{
			annotations.put(annotationId, annotation);
		}
	}

	/**
	 * This method removes an annotation from this node.
	 * 
	 * @param annotationId The id of the annotation to remove. If such an
	 *            annotation is not present, nothing happens.
	 * @return The removed annotation, or <code>null</code> when no such
	 *         annotation was present.
	 * @exception NullPointerException if annotationId is <code>null</code>
	 */
	public Object removeAnnotation(String annotationId)
	{
		return annotations.remove(annotationId);
	}

	/*
	 * (non-Javadoc)
	 * @see groove.graph.DefaultNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("AnnotatedNode\n");
		buffer.append("  Annotations:\n");
		for (String key : annotations.keySet())
		{
			buffer.append("  - ");
			buffer.append(key);
			buffer.append('\n');
		}

		return buffer.toString();
	}
}
