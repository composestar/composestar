/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class ExecutionModel extends RepositoryEntity implements Cloneable{
    private Hashtable entranceStates;
    private Hashtable exitStates;
    private HashSet states;
    private HashSet transitions;
    
//    /**
//     * This vector contains all transitions from a state which has not the
//     * star-selector to a state which has the star selector. These transitions
//     * are also contained in the transitions-Vector. This Vector is used to
//     * make the copyStarTrace algorithm more efficient.
//     */
//    private Vector starTransitions;
    
    public ExecutionModel(){
        super();
        
        entranceStates = new Hashtable();
        exitStates = new Hashtable();
        states = new HashSet();
        transitions = new HashSet();
    }
    
    /**
     * Adds an entrance state to this model. The method addState should also
     * be used on this state.
     * @param state
     */
    public void addEntranceState( ExecutionState state ){
        entranceStates.put( state.getMessage(), state );
    }
    
    public Enumeration getEntranceStates(){
        return entranceStates.elements();
    }
    
    /**
     * Returns the entrance state for the given selector. If a selector
     * doesn't have it's own entrance state, the entrance state of the star-trace
     * is returned.
     * @param message
     * @return
     */
    public ExecutionState getEntranceState( Message message ){
        ExecutionState state = (ExecutionState) entranceStates.get( message );
        
        if ( state == null ){
            state = (ExecutionState) entranceStates.get(
                    new Message( Message.STAR_TARGET, message.getSelector() ) );
        }
        if ( state == null ){
            state = (ExecutionState) entranceStates.get(
                    new Message( message.getTarget(), Message.STAR_SELECTOR ) );
        }
        if ( state == null ){
            state = (ExecutionState) entranceStates.get( Message.STAR_MESSAGE );
        }
        
        return state;
    }
    
    /**
     * Adds an exit state to this model. The method addState should also
     * be used on this state.
     * @param state
     */
    public void addExitState( ExecutionState state ){
        exitStates.put( state.getMessage(), state );
    }
    
    public Enumeration getExitStates(){
        return exitStates.elements();
    }
    
    /**
     * Returns the exit state of the selector. If the selector belongs
     * to the star-trace (it can't be discriminated in the FilterModule) the
     * star-trace exit is returned, if existing. Else <code>null</code> is 
     * returned.
     * @param selector
     * @return
     */
    public ExecutionState getExitState( Message message ){
        ExecutionState state = (ExecutionState) exitStates.get( message );
        
        //if state is null and the selector enters through a star-trace,
        //return the star-trace exit:
        if ( state == null ){
            state = (ExecutionState) exitStates.get(
                    new Message( Message.STAR_TARGET, message.getSelector() ) );
        }
        if ( state == null ){
            state = (ExecutionState) exitStates.get(
                    new Message( message.getTarget(), Message.STAR_SELECTOR ) );
        }
        if ( state == null ){
            state = (ExecutionState) exitStates.get( Message.STAR_MESSAGE );
        }
        
        return state;
    }
    
    /**
     * Adds a normal state to this model (for boundary states use the method
     * <code>addBoundaryState( ExecutionState )</code> instead.
     * @param state
     */
    public void addState( ExecutionState state ){
        states.add( state );
    }
    
    
    
    /**
     * Adds a transition to this model.
     * @param transition
     */
    public void addTransition( ExecutionTransition transition ){
        transitions.add( transition );
        
//        if ( !transition.getStartState().getSelector().equals( "*" )
//                &&
//                transition.getEndState().getSelector().equals( "*" ) )
//        {
//            starTransitions.addElement( transition );
//        }
    }
    
    /**
     * Returns all the selectors for which there are different 
     * entrance states.
     * @return
     */
    public Set getEntranceSelectors(){
//        return (String[]) entranceStates.keySet().toArray( new String[0] );
        return entranceStates.keySet();
    }
    
    
    
    public boolean isEntranceSelector( Message message ){
        return entranceStates.containsKey( message );
    }
    
    /**
     * Returns all the selectors for which there are different 
     * exit states.
     * @return
     */
    public Set getExitSelectors(){
//        return (String[]) exitStates.keySet().toArray( new String[0] );
        return exitStates.keySet();
    }
    
    public boolean isExitSelector( Message message ){
        return exitStates.containsKey( message );
    }
    
//    /**
//     * Returns a copy of this executionmodel which is expanded to the given
//     * array of selectors. This means that when a selector in the set doesn't
//     * have a trace in the model, a copy of the star-trace is generated for this
//     * selector. When some selector is not in the array, but has a trace, this trace
//     * is maintained.
//     * @param selectors
//     */
//    public ExecutionModel expand( String[] selectors ){
//        ExecutionModel model;
//        try {
//            model = (ExecutionModel) this.clone();
//        } catch (CloneNotSupportedException e) {
//            //should not occur
//            e.printStackTrace();
//            return null;
//        }
//        
//        model.doExpand( selectors );
//        return model;
//    }
//    
//    /**
//     * This method does the actual expanding to the current object.
//     * @param selectors
//     * @see ExecutionModel.expand( String[] )
//     */
//    private void doExpand( String[] selectors ){
//        for (int i=0; i<selectors.length; i++){
//            if ( !entranceStates.containsKey( selectors[i] ) )
//                copyStarTrace( selectors[i] );
//        }
//    }
//    
//    /**
//     * Copies to star-trace to generate the trace for the new selector.
//     * @param newSelector
//     */
//    private void copyStarTrace( String newSelector ){
//        ExecutionState state, startState, endState;
//        ExecutionTransition transition, newTransition;
//        Hashtable visitedStates;
//        
//        state = (ExecutionState) entranceStates.get( "*" );
//        
//        visitedStates = new Hashtable();
//        
//        this.addEntranceState( 
//                copyStarTraceState( newSelector, state, visitedStates ) );
//        
////        //add incoming transitions:
////        for (int i=0; i<starTransitions.size(); i++){
////            transition = (ExecutionTransition) starTransitions.elementAt( i );
////            startState = transition.getStartState();
////            endState = (ExecutionState) visitedStates.get( transition.getEndState() );
////            newTransition = new ExecutionTransition( startState, 
////                    transition.getLabel(), endState, transition.getFlowTransition() );
////            
////            this.addTransition( newTransition );
////        }
//    }
//    
//    /**
//     * Copies a state in the trace. Calls recursively all next states.
//     * @param newSelector
//     * @param state
//     * @param visitedStates
//     * @return
//     */
//    private ExecutionState copyStarTraceState( String newSelector, 
//            ExecutionState state, Hashtable visitedStates )
//    {
//        ExecutionState newState;
//        Enumeration transitions;
//        ExecutionTransition transition;
//        ExecutionState endState;
//        ExecutionState newEndState;
//        ExecutionTransition newTransition;
//        
//        
//        newState = new ExecutionState( state.getFlowNode(), newSelector, 
//                state.getStateType() );
//        visitedStates.put( state, newState );
//        addState( newState );
//        
//        transitions = state.getOutTransitions();
//        
//        //check if the currentnode is an exit node:
//        if ( !transitions.hasMoreElements() ){
//            if ( exitStates.get( state.getSelector() ) == state ){
//                this.addExitState( newState );
//            }
//        }
//        
//        //copy transitions:
//        while( transitions.hasMoreElements() ){
//            transition = (ExecutionTransition) transitions.nextElement();
//            endState = transition.getEndState();
//            if ( endState.getSelector().equals( "*" ) ){
//                if ( visitedStates.containsKey( endState ) ){
//                    newEndState = (ExecutionState) visitedStates.get( endState );
//                }
//                else{
//                    newEndState = copyStarTraceState( 
//                            newSelector, endState, visitedStates );
//                }
//                newTransition = new ExecutionTransition( 
//                        newState, 
//                        transition.getLabel(), 
//                        newEndState, 
//                        transition.getFlowTransition()
//                        );
//            }
//            else{
//                newTransition = new ExecutionTransition(
//                        newState,
//                        transition.getLabel(),
//                        endState,
//                        transition.getFlowTransition()
//                        );
//            }
//            
//            this.addTransition( newTransition );
//        }
//        
//        //add incoming transitions from outside the startrace:
//        Enumeration enum = state.getInTransitions();
//        while (enum.hasMoreElements()){
//            transition = (ExecutionTransition) enum.nextElement();
//            if ( !transition.getStartState().getSelector().equals( "*" ) ){
//                newTransition = new ExecutionTransition( 
//                        transition.getStartState(), transition.getLabel(), 
//                        newState, transition.getFlowTransition() );
//                this.addTransition( newTransition );
//            }
//        }
//        
//        return newState;
//    }
    
    
//    public void addExecution( ExecutionModel model ){
//        //remove unreachable traces:
//        String[] entranceSelectors = model.getEntranceSelectors();
//        for (int i=0; i<entranceSelectors.length; i++){
//            if ( !exitStates.containsKey( entranceSelectors[i] ) ){
//                model.removeTrace( entranceSelectors[i] );
//            }
//        }
//        
//        //couple traces:
//        String[] selectors = this.getExitSelectors();
//        for (int i=0; i<selectors.length; i++){
//            
//        }
//    }
    
    
    public void removeTrace( Message message ){
        ExecutionState state;
        
        state = (ExecutionState) entranceStates.get( message );
        
        removeState( state );
    }
    
    private void removeState( ExecutionState state ){
        ExecutionTransition transition;
        ExecutionState endState;
        
        if ( state == null )
            return;
        
        
        Enumeration enum = state.getOutTransitions();
        while( enum.hasMoreElements() ){
            transition = (ExecutionTransition) enum.nextElement();
            endState = transition.getEndState();
            endState.removeInTransition( transition );
            
            this.transitions.remove( transition );
//            this.starTransitions.removeElement( transition );
            
            if ( endState != state  &&  
                    !endState.getInTransitions().hasMoreElements() )
            {
                removeState( endState );
            }
        }
        
        this.states.remove( state );
        if ( state.getStateType() == ExecutionState.ENTRANCE_STATE ){
            entranceStates.remove( state.getMessage() );
        }
        else if ( state.getStateType() == ExecutionState.EXIT_STATE ){
            exitStates.remove( state.getMessage() );
        }
    }
    
    
    /**
     * Clones this object and also its referenced states and transitions.
     * @throws CloneNotSupportedException
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException{
        ExecutionModel model;
        ExecutionState state, newState, startState, endState, entranceState, exitState;
        ExecutionTransition transition, newTransition;
        Hashtable newStates;
        
        
        model = (ExecutionModel) super.clone();
        
        model.entranceStates = new Hashtable();
        model.exitStates = new Hashtable();
        model.states = new HashSet();
        model.transitions = new HashSet();
        
        newStates = new Hashtable();
        
        //clone states:
        Iterator iter = this.states.iterator();
        while ( iter.hasNext() ){
            state = (ExecutionState) iter.next();
            newState = (ExecutionState) state.clone();
            newStates.put( state, newState );
            model.addState( newState );
        }
        
        //clone transitions:
        iter = this.transitions.iterator();
        while( iter.hasNext() ){
            transition = (ExecutionTransition) iter.next();
            startState = (ExecutionState) newStates.get( transition.getStartState() );
            endState = (ExecutionState) newStates.get( transition.getEndState() );
            newTransition = new ExecutionTransition( startState, 
                    transition.getLabel(), endState, transition.getFlowTransition() );
            model.addTransition( newTransition );
        }
        
        //add entrance states:
        Enumeration enum = this.entranceStates.elements();
        while( enum.hasMoreElements() ){
            entranceState = (ExecutionState) enum.nextElement();
            entranceState = (ExecutionState) newStates.get( entranceState );
            model.addEntranceState( entranceState );
        }
        
        //add entrance states:
        enum = this.exitStates.elements();
        while( enum.hasMoreElements() ){
            exitState = (ExecutionState) enum.nextElement();
            exitState = (ExecutionState) newStates.get( exitState );
            model.addExitState( exitState );
        }
        
        return model;
    }
}
