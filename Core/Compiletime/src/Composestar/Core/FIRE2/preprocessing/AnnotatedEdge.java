/*
 * Created on 21-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.BinaryEdge;
import groove.graph.DefaultEdge;
import groove.graph.Label;
import groove.graph.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arjan de Roo
 */
public class AnnotatedEdge extends DefaultEdge
{
	private static final long serialVersionUID = 2512401433673944080L;

	/**
	 * Dictionary containing the annotations.
	 */
	private Map<String, Object> annotations;

	public AnnotatedEdge(Node startNode, String label, Node endNode)
	{
		super(startNode, label, endNode);
		annotations = new HashMap<String, Object>();
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

	/**
	 * This implementation returns a {@link AnnotatedEdge}.
	 */
	@Override
	public BinaryEdge newEdge(Node source, Label label, Node target)
	{
		return new AnnotatedEdge(source, label, target);
	}
}
