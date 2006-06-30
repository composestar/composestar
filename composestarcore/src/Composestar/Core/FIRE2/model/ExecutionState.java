/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class ExecutionState implements Cloneable{
    private FlowNode flowNode;
    private Target substitutionTarget;
    private MessageSelector substitutionSelector;
    private Message message;
    private Vector outTransitions;
    private Vector inTransitions;
    private int stateType;
    
    public final static int ENTRANCE_STATE = 1;
    public final static int EXIT_STATE = 2;
    public final static int NORMAL_STATE = 3;
    
    public ExecutionState( FlowNode flowNode, MessageSelector selector, Target target, 
            MessageSelector substitutionSelector, Target substitutionTarget, 
            int stateType )
    {
        super();
        
        this.flowNode = flowNode;
        this.message = new Message( target, selector );
        this.substitutionSelector = substitutionSelector;
        this.substitutionTarget = substitutionTarget;
        this.stateType = stateType;
        
        outTransitions = new Vector();
        inTransitions = new Vector();
    }
    
    
    /**
     * @return Returns the flowNode.
     */
    public FlowNode getFlowNode() {
        return flowNode;
    }
    
    /**
     * @return Returns the selector.
     */
    public MessageSelector getSelector() {
        return message.getSelector();
    }
    
    /**
     * @return Returns the substitutionSelector.
     */
    public MessageSelector getSubstitutionSelector() {
        return substitutionSelector;
    }
    
    /**
     * @return Returns the substitutionTarget.
     */
    public Target getSubstitutionTarget() {
        return substitutionTarget;
    }
    
    /**
     * @return Returns the target.
     */
    public Target getTarget() {
        return message.getTarget();
    }
    
    
    /**
     * @return Returns the message.
     */
    public Message getMessage() {
        return message;
    }
    
    
    public void addOutTransition( ExecutionTransition transition ){
        outTransitions.addElement( transition );
    }
    
    public void removeOutTransition( ExecutionTransition transition ){
        outTransitions.removeElement( transition );
    }
    
    public Enumeration getOutTransitions(){
        return outTransitions.elements();
    }
    
    public boolean hasOutTransitions(){
        return !outTransitions.isEmpty();
    }
    
    public void addInTransition( ExecutionTransition transition ){
        inTransitions.addElement( transition );
    }
    
    public void removeInTransition( ExecutionTransition transition ){
        inTransitions.removeElement( transition );
    }
    
    public Enumeration getInTransitions(){
        return inTransitions.elements();
    }
    
    
    /**
     * @return Returns the stateType.
     */
    public int getStateType() {
        return stateType;
    }
    
    
    public Object clone() throws CloneNotSupportedException{
        ExecutionState state;
        
        state = (ExecutionState) super.clone();
        
        state.flowNode = this.flowNode;
        
        state.message = (Message) this.message.clone();
        
        state.substitutionTarget = this.substitutionTarget;
        state.substitutionSelector = this.substitutionSelector;
        
        state.outTransitions = new Vector();
        state.inTransitions = new Vector();
        
        state.stateType = this.stateType;
        
        return state;
    }
    
    public ExecutionState clone( Message newMessage ){
        ExecutionState state = new ExecutionState(flowNode, newMessage.getSelector(),
                newMessage.getTarget(), substitutionSelector, substitutionTarget, 
                stateType );
        
        state.outTransitions = this.outTransitions;
        state.inTransitions = this.inTransitions;
        return state;
    }
    
    
    
    public int hashCode() {
        int hashCode = flowNode.hashCode() + message.hashCode();
        
        return hashCode;
    }
    
    
    public boolean equals( Object obj ){
        if ( !(obj instanceof ExecutionState) )
            return false;
        
        ExecutionState state = (ExecutionState) obj;
        if ( this.flowNode != state.flowNode )
            return false;
        
        if ( !Message.checkEquals( this.substitutionTarget, state.substitutionTarget) )
            return false;
        
        if ( !Message.checkEquals( this.substitutionSelector, 
                state.substitutionSelector ) )
        {
            return false;
        }
         
        if ( !this.message.equals( state.message ) )
            return false;
        
        //other fields not important for comparison
        
        return true;
    }
}
