/*
 * Created on 20-feb-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * This class is a node in a graph, which has some annotation.
 * 
 * @author Arjan de Roo
 */
public class FlowNode{
    private int type;
    
    private HashSet names;
    
	private RepositoryEntity repositoryLink;
	
	/**
	 * Contains all transitions originating from this node.
	 */
	private Vector transitions;
	
	/**
	 * Hashtable to add annotations to a node, which don't need to be
	 * serialized. Useful for algorithms to store some processing information.
	 */
	private transient Hashtable annotations = new Hashtable();
	
	
	public final static int CONTEXT_NODE = 1;
	public final static int PROCEDURE_NODE = 2;
	public final static int PREDICATE_NODE = 3;
	public final static int ACTION_NODE = 4;
	
	
	/**
	 * Default constructor
	 *
	 */
	public FlowNode( int type, HashSet names, RepositoryEntity repositoryLink ){
		super();
		
		this.type = type;
		this.names = names;
		this.repositoryLink = repositoryLink;
		
		transitions = new Vector();
	}
	
	public void addTransition( FlowTransition transition ){
	    transitions.addElement( transition );
	}
	
	public void removeTransition( FlowTransition transition ){
	    transitions.removeElement( transition );
	}
	
	public Enumeration getTransitions(){
	    return transitions.elements();
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
	 * This method adds an annotation to this node. This annotation is only
	 * temporary. It won´t be persisted during serialization/deserialization
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
     * Returns the (first) transition from this startnode to the given endnode, or
     * null when no such transition exists.
     * 
     * @return
     */
    public FlowTransition getTransition(FlowNode endNode) {
        FlowTransition transition;
        Enumeration enum = transitions.elements();
        
        while( enum.hasMoreElements() ){
            transition = (FlowTransition) enum.nextElement();
            if ( transition.getEndNode() == endNode ){
                return transition;
            }
        }
        
        return null;
    }
    
    
    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    
    
    /**
     * @return Returns the names.
     */
    public Iterator getNames() {
        return names.iterator();
    }
    
    public boolean containsName( String name ){
        return names.contains( name );
    }
    
    /**
     * @return Returns the repositoryLink.
     */
    public RepositoryEntity getRepositoryLink() {
        return repositoryLink;
    }
}
