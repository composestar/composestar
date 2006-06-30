/*
 * Created on 10-mrt-2006
 *
 */
package Composestar.Core.FIRE2.analysis;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.LAMA.MethodInfo;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class DepthFirstIterator implements Iterator{
    private FireAnalysis model;
    private int signatureCheck = FireAnalysis.NO_SIGNATURE_CHECK;
    private MethodInfo methodInfo;
    public Stack unvisitedStates = new Stack();
    public HashSet visitedStates = new HashSet();
    
    public DepthFirstIterator( FireAnalysis model ){
        this.model = model;
        
        ExecutionState[] states = model.getStartStates();
        for (int i=0; i<states.length; i++){
            unvisitedStates.push( states[i] );
        }
    }
    
    public DepthFirstIterator( FireAnalysis model, String selector ){
        this.model = model;
        
        unvisitedStates.push( model.getStartState( selector ) );
    }
    
    public DepthFirstIterator( FireAnalysis model, MethodInfo methodInfo, 
            int signatureCheck )
    {
        this.model = model;
        this.methodInfo = methodInfo;
        this.signatureCheck = signatureCheck;
        
        unvisitedStates.push( 
                model.getStartState( methodInfo, signatureCheck ) );
    }
        
    
    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return !unvisitedStates.isEmpty();
    }
    
    
    
    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
        ExecutionState state;
        
        if ( !hasNext() ){
            throw new NoSuchElementException();
        }
        
        //get next state:
        state = (ExecutionState) unvisitedStates.pop();
        
        
        //add the state to the visitedStates, if it is not a FilterAction.
        //To ensure that a filteraction can be reached from different
        //matching/substition parts (important for SIGN)
        if ( !state.getFlowNode().containsName( "FilterAction" ) )
            visitedStates.add( state );
        
        
        //add next unvisitedStates to the unvisitedStates stack:
        addNextStates( state );
        
        return state;
    }
    
    /**
     * adds the next states of the given state to the unvisitedstates stack.
     * @param state
     */
    private void addNextStates( ExecutionState state ){
        ExecutionState nextState;
        ExecutionState substitutionPartState = null;
        
        
        Enumeration enum = state.getOutTransitions();
        while( enum.hasMoreElements() ){
            ExecutionTransition transition = (ExecutionTransition) enum.nextElement();
            nextState = transition.getEndState();
            
            if ( nextState.getFlowNode().containsName( "SubstitutionPart" ) )
                substitutionPartState = nextState;
            else if ( !visitedStates.contains( nextState ) ){
                unvisitedStates.push( nextState );
            }
                
        }
        
        //ensure that substitutionpart is visited first:
        if ( substitutionPartState != null  &&  
                !visitedStates.contains( substitutionPartState) )
        {
            unvisitedStates.push( substitutionPartState );
        }
    }
    
    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
