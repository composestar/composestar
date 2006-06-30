/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Dictionary;


/**
 * 
 *
 * @author Arjan de Roo
 */
public class ExecutionTransition{
    /**
     * The label of this transition.
     */
    private String label;
    
    /**
     * The startState
     */
	private ExecutionState startState;
	
	/**
	 * The endState
	 */
	private ExecutionState endState;
	
	/**
	 * The corresponding flowTransition;
	 */
	private FlowTransition flowTransition;
	
	/**
	 * A dictionary to add annotations to this transition.
	 */
	private Dictionary annotations;
	
	public ExecutionTransition( ExecutionState startState, String label, 
	        ExecutionState endState, FlowTransition flowTransition )
	{
	    super();
	    
	    this.startState = startState;
	    this.label = label;
	    this.endState = endState;
	    this.flowTransition = flowTransition;
	    
	    startState.addOutTransition( this );
	    endState.addInTransition( this );
	}
	
	
	
    /**
     * @return Returns the endState.
     */
    public ExecutionState getEndState() {
        return endState;
    }
    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }
    /**
     * @return Returns the startState.
     */
    public ExecutionState getStartState() {
        return startState;
    }
    
    
    /**
     * @return Returns the flowTransition.
     */
    public FlowTransition getFlowTransition() {
        return flowTransition;
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
	 * This method adds an annotation to this transition.
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
	 * This method removes an annotation from this transition.
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
}
