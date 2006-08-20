/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;




/**
 * 
 *
 * @author Arjan de Roo
 */
public abstract class ExecutionTransition{
    /**
     * The label of this transition.
     */
    private String label;
    
    /**
     * The corresponding flowTransition;
     */
    private FlowTransition flowTransition;
	
    
    public ExecutionTransition( String label, FlowTransition flowTransition )
    {
        super();
        
        this.label = label;
        this.flowTransition = flowTransition;
    }
    
    
    
    
    
    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * @return Returns the flowTransition.
     */
    public FlowTransition getFlowTransition() {
        return flowTransition;
    }
    
    /**
     * @return Returns the startState.
     */
    public abstract ExecutionState getStartState();
    
    /**
     * @return Returns the endState.
     */
    public abstract ExecutionState getEndState();
    
}
