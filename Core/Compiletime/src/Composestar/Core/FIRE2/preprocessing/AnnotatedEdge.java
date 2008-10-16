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

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.Label;
import groove.graph.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arjan de Roo
 */
public class AnnotatedEdge extends AbstractBinaryEdge<Node, Label, Node>
{
	private static final long serialVersionUID = 2512401433673944080L;

	/**
	 * Dictionary containing the annotations.
	 */
	private Map<String, Object> annotations;

	/**
	 * @deprecated Use {@link #AnnotatedEdge(Node, Label, Node)}
	 */
	@Deprecated
	public AnnotatedEdge(Node startNode, String label, Node endNode)
	{
		this(startNode, DefaultLabel.createLabel(label), endNode);
	}

	public AnnotatedEdge(Node startNode, Label label, Node endNode)
	{
		super(startNode, label, endNode);
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
}
