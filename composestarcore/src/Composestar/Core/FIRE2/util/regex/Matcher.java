/*
 * Created on 30-mei-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.util.regex.Pattern.RegularState;
import Composestar.Core.FIRE2.util.regex.Pattern.RegularTransition;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class Matcher{
    private Pattern pattern;
    private ExecutionModel model;
    private Labeler labeler;
    
    private HashSet processedStates;
    private Stack unvisitedStates;
    private CombinedState endState;
    private boolean matchDone = false;
    
    
    public Matcher( Pattern pattern, ExecutionModel model, Labeler labeler )
    {
        this.pattern = pattern;
        this.model = model;
        this.labeler = labeler;
    }
    
    public int processedStatesCount(){
        return processedStates.size();
    }
    
    public boolean matches(){
        matchDone = true;
        initialize();
        return process();
    }
    
    
    /**
     * initializes start states.
     *
     */
    private void initialize(){
        processedStates = new HashSet();
        unvisitedStates = new Stack();
        
        Enumeration states = model.getEntranceStates();
        RegularState regularState = pattern.getStartState();
        while( states.hasMoreElements() ){
            ExecutionState state = (ExecutionState) states.nextElement();
            addState( new CombinedState( state, regularState ) );
        }
    }
    
    private boolean process(){
        while( !unvisitedStates.empty() ){
            CombinedState state = (CombinedState) unvisitedStates.pop();
            if ( process( state ) )
                return true;
        }
        
        return false;
    }
    
    private boolean process( CombinedState state ){
        Enumeration regularTransitions;
        RegularState regularState;
        RegularTransition regularTransition;
        RegularState[] nextStates;
        
        Enumeration executionTransitions;
        ExecutionState executionState;
        ExecutionTransition executionTransition;
        
        LabelSequence sequence;
        
        CombinedState newState;
        
        
        //empty transition in regular machine:
        regularState = state.regularState;
        regularTransitions = regularState.getOutTransitions();
        while( regularTransitions.hasMoreElements() ){
            regularTransition = (RegularTransition) regularTransitions.nextElement();
            if ( regularTransition.isEmpty() ){
                newState = new CombinedState( 
                        state.executionState, regularTransition.getEndState(), state );
                if ( isEndState( newState ) )
                    return true;
                else
                    addState( newState );
            }
        }
        
        //empty and non-empty transition in ExecutionModel:
        executionState = state.executionState;
        executionTransitions = executionState.getOutTransitions();
        while( executionTransitions.hasMoreElements() ){
            executionTransition = 
                (ExecutionTransition) executionTransitions.nextElement();
            sequence = labeler.getLabels( executionTransition );
            if ( sequence.isEmpty() ){
                newState = new CombinedState( 
                        executionTransition.getEndState(), state.regularState,
                        state, executionTransition );
                if ( isEndState( newState ) )
                    return true;
                else
                    addState( newState );
            }
            else{
                nextStates = getNextStates( regularState, sequence );
                for (int i=0; i<nextStates.length; i++){
                    newState = new CombinedState(
                            executionTransition.getEndState(), nextStates[i],
                            state, executionTransition );
                    if ( isEndState( newState ) )
                        return true;
                    else
                        addState( newState );
                }
            }
        }
        
        
        
        return false;
    }
    
    
    private RegularState[] getNextStates( RegularState state, 
            LabelSequence sequence )
    {
        RegularState[] currentStates = { state };
        Vector v = new Vector();
        
        Enumeration resOperSeq = sequence.getResourceOperationSequences();
        while( resOperSeq.hasMoreElements() ){
            String resOper = (String) resOperSeq.nextElement();
            
            for (int i=0; i<currentStates.length; i++){
                v.addAll( getNextStates( currentStates[i], resOper ) );
            }
            
            currentStates = (RegularState[]) v.toArray( new RegularState[v.size()] );
        }
        
        return currentStates;
    }
    
    private Collection getNextStates( RegularState state, String resourceOperation ){
        Enumeration transitions = state.getOutTransitions();
        HashSet result = new HashSet();
        while( transitions.hasMoreElements() ){
            RegularTransition transition = 
                (RegularTransition) transitions.nextElement();
            if ( transition.match( resourceOperation ) ){
                result.addAll( lambdaClosure( transition.getEndState() ) );
            }
        }
        
        return result;
    }
    
    /**
     * Returns the lambda (empty transition) closure of the given state, i.e.
     * all states reachable from the given state (including this given state) that
     * are reachable with empty transitions.
     * @param state The starting state.
     * @return The lambda closure of the given state.
     */
    private Collection lambdaClosure( RegularState state ){
        RegularTransition transition;
        Enumeration transitions;
        HashSet result;
        Stack checkNext;
        RegularState currentState, nextState;
        
        result = new HashSet();
        result.add( state );
        
        checkNext = new Stack();
        checkNext.push( state );
        
        while( !checkNext.isEmpty() ){
            currentState = (RegularState) checkNext.pop();
            transitions = currentState.getOutTransitions();
            while( transitions.hasMoreElements() ){
                transition = (RegularTransition) transitions.nextElement();
                nextState = transition.getEndState();
                if ( transition.isEmpty()  &&  !result.contains( nextState ) )
                {
                    result.add( nextState );
                    checkNext.push( nextState );
                }
            }
        }
        
        return result;
    }
    
    private void addState( CombinedState state ){
        if ( !processedStates.contains( state ) ){
            processedStates.add( state );
            unvisitedStates.push( state );
        }
    }
    
    private boolean isEndState( CombinedState state ){
        if( state.regularState == pattern.getEndState()  &&
        	!state.executionState.getOutTransitions().hasMoreElements() )
        {
            endState = state;
            return true;
        }
        else{
            return false;
        }
    }
    
    public Enumeration matchTrace(){
        if ( !matchDone ){
            matches();
        }
        
        if ( endState != null ){
            return endState.trace.toVector().elements();
        }
        else{
            return null;
        }
    }
    
    private class CombinedState{
        public ExecutionState executionState;
        public Pattern.RegularState regularState;
//        private Vector trace;
        private TransitionTrace trace;
        
        public CombinedState( ExecutionState executionState, 
                RegularState regularState )
        {
            this.executionState = executionState;
            this.regularState = regularState;
            trace = new TransitionTrace();
//            trace = new Vector();
        }
        
        public CombinedState( ExecutionState executionState,
                RegularState regularState, CombinedState previousState )
        {
            this.executionState = executionState;
            this.regularState = regularState;
            trace = previousState.trace;
//            trace = new Vector();
//            trace.addAll( previousState.trace );
        }
        
        public CombinedState( ExecutionState executionState,
                RegularState regularState, CombinedState previousState,
                ExecutionTransition transition )
        {
            this.executionState = executionState;
            this.regularState = regularState;
            trace = new TransitionTrace( previousState.trace, transition );
//            trace = new Vector();
//            trace.addAll( previousState.trace );
//            trace.add( transition );
        }
        
        
        public boolean equals(Object obj) {
            if ( !(obj instanceof CombinedState) )
                return false;
            
            CombinedState state = (CombinedState) obj;
            
            return this.executionState.equals( state.executionState )  &&
            	this.regularState.equals( state.regularState );
        }
        
        
        public int hashCode() {
            return executionState.hashCode() + regularState.hashCode();
        }
    }
    
    
    /**
     * LinkedList kind of class to efficiently construct the trace during
     * matching.
     * 
     *
     * @author Arjan de Roo
     */
    private class TransitionTrace{
        public TransitionTrace heading;
        public ExecutionTransition last;
        
        public TransitionTrace(){
            
        }
        
        public TransitionTrace( TransitionTrace heading, ExecutionTransition last ){
            this.heading = heading;
            this.last = last;
        }
        
        public Vector toVector(){
            if ( heading == null ){
                return new Vector();
            }
            else{
                Vector v = heading.toVector();
                v.addElement( last );
                return v;
            }
        }
    }
}
