/*
 * Created on 21-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.DefaultNode;
import groove.graph.Node;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class AnnotatedNode extends DefaultNode {
    /**
     * Dictionary containing the annotations.
     */
    private Dictionary annotations;
    
    
    public AnnotatedNode(){
        annotations = new Hashtable();
    }
    
    /**
	 * This method returns the annotation corresponding with the given annotationId.
	 * 
	 * @param annotationId The id of the annotation.
	 * 
	 * @return The annotation corresponding with the annotationId, or <code>null</code>
	 * when no annotation corresponds with the id.
	 * 
	 * @exception NullPointerException if the annotationId is <code>null</code>
	 */
	public Object getAnnotation( String annotationId ){
		return annotations.get( annotationId );
	}
	
	/**
	 * This method adds an annotation to this node.
	 * 
	 * @param annotationId The id of the annotation.
	 * @param annotation The annotation. If this is <code>null</code> this method
	 * has the same behaviour as <code>removeAnnotation( annotationId )</code>
	 * 
	 * @exception NullPointerException if annotationId is <code>null</code>
	 * 
	 * @see AnnotatedNode.removeAnnotation( String )
	 */
	public void addAnnotation( String annotationId, Object annotation ){
		if ( annotation == null ){
			removeAnnotation( annotationId );
		}
		else{
			annotations.put( annotationId, annotation );
		}
	}
	
	/**
	 * This method removes an annotation from this node.
	 * 
	 * @param annotationId The id of the annotation to remove. If such an annotation
	 * is not present, nothing happens.
	 * 
	 * @return The removed annotation, or <code>null</code> when no such annotation
	 * was present.
	 * 
	 * @exception NullPointerException if annotationId is <code>null</code>
	 */
	public Object removeAnnotation( String annotationId ){
		return annotations.remove( annotationId );
	}
	

	
    /**
     * @see groove.graph.Node#newNode()
     */
    public Node newNode() {
        return new AnnotatedNode();
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append( "AnnotatedNode\n" );
        buffer.append( "  Annotations:\n" );
        //enum gives problems in JDK1.5
        Enumeration enumer = annotations.elements();
        while( enumer.hasMoreElements() ){
            buffer.append( "  - " );
            buffer.append( enumer.nextElement() );
            buffer.append('\n' );
        }
        
        return buffer.toString();
    }
}
