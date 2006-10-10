/*
 * Created on 23-aug-2006
 *
 */
package Composestar.Core.INLINE.highlevel;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.FIRE2.model.ExecutionLabels;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.util.queryengine.predicates.StateType;
import Composestar.Core.LAMA.MethodInfo;

public class HighLevelInliner {
    private HighLevelInlineStrategy strategy;
    private ConditionalInliner conditionalInliner;
    
    private StateType isFilterModule;
    private StateType isCondExpr;
    private StateType isFilter;
    private StateType isAction;
    private StateType isEnd;
    
    private ExecutionModel currentModel;
    
    public HighLevelInliner( HighLevelInlineStrategy strategy ){
        this.strategy = strategy;
        conditionalInliner = new ConditionalInliner( strategy );
        
        initialize();
    }
    
    private void initialize(){
        isFilter = new StateType( FlowChartNames.FILTER_NODE );
        isCondExpr =
            new StateType( FlowChartNames.CONDITION_EXPRESSION_NODE ); 
        isAction = new StateType( FlowChartNames.ACTION_NODE );
        isFilterModule = new StateType( FlowChartNames.FILTER_MODULE_NODE );
        isEnd = new StateType( FlowChartNames.END_NODE );
    }
    
    public void inline( ExecutionModel model, FilterModule[] modules, 
            MethodInfo method )
    {
        this.currentModel = model;
        
        try{
            Vector blocks = identifyFilterModuleBlocks( model );
        
            inline( blocks, modules, method );
        }
        catch( InlineException exc ){
            //when an InlineException occurs(filter cloning through substitution),
            //use conditional inliner:
            conditionalInliner.inline( model, modules, method );
        }
    }
    
    
    private void inline( Vector blocks, FilterModule[] modules, MethodInfo method ){
        strategy.startInline( modules, method );
        
        for (int i=0; i<blocks.size(); i++){
            inlineFilterModuleBlock( (FilterModuleBlock) blocks.elementAt(i) );
        }
        
        strategy.endInline();
    }
    
    private void inlineFilterModuleBlock( FilterModuleBlock fmBlock ){
        strategy.startFilterModule( 
                (FilterModule) fmBlock.fmState.getFlowNode().getRepositoryLink() );
        
        for (int i=0; i<fmBlock.filters.size(); i++){
            inlineFilterBlock( (FilterBlock) fmBlock.filters.elementAt(i) );
        }
        
        strategy.endFilterModule();
    }
    
    private void inlineFilterBlock( FilterBlock filterBlock ){
        Enumeration filterElements = filterBlock.filterElements.elements();
        
        if ( filterElements.hasMoreElements() )
            inlineFilterElements( filterElements );
    }
    
    private void inlineFilterElements( Enumeration filterElements ){
        FilterElementBlock filterElement = 
            (FilterElementBlock) filterElements.nextElement();
        
        ExecutionState flowFalseExitState = filterElement.flowFalseExitState;
        ExecutionState flowTrueExitState = filterElement.flowTrueExitState;
        
        if ( flowFalseExitState == null ){
            //flueTrue can't be null if flowFalse is null
            
            if ( isCondExpr.isTrue( flowTrueExitState ) ){
                //continue with next filter element:
                inlineFilterElements( filterElements );
            }
            else{
                strategy.generateAction( flowTrueExitState );
            }
        }
        else if ( isCondExpr.isTrue( flowFalseExitState ) ){
            if ( flowTrueExitState == null ){
                //continue with next filter element:
                inlineFilterElements( filterElements );
            }
            else if ( isCondExpr.isTrue( flowTrueExitState ) ){
                //continue with next filter element:
                inlineFilterElements( filterElements );
            }
            else{
                ExecutionState conditionExprState = filterElement.conditionExprState;
                FlowNode condExprFlowNode = conditionExprState.getFlowNode();
                
                strategy.openBranchTrue( 
                        (ConditionExpression) condExprFlowNode.getRepositoryLink() );
                
                strategy.generateAction( flowTrueExitState );
                
                strategy.closeBranchTrue();
                strategy.openBranchFalse();
                
                //do the inlining of the next filter elements:
                inlineFilterElements( filterElements );
                
                strategy.closeBranchFalse();
            }
        }
        else{
            if ( flowTrueExitState == null ){
                strategy.generateAction( flowFalseExitState );
            }
            else if ( isCondExpr.isTrue( flowTrueExitState ) ){
                //not possible
            }
            else{
                if ( flowFalseExitState.equals( flowTrueExitState ) ){
                    strategy.generateAction( flowFalseExitState );
                }
                else{
                    ExecutionState conditionExprState = 
                        filterElement.conditionExprState;
                    FlowNode condExprFlowNode = conditionExprState.getFlowNode();
                    
                    strategy.openBranchTrue( 
                            (ConditionExpression) condExprFlowNode.getRepositoryLink() );
                    
                    strategy.generateAction( flowTrueExitState );
                    
                    strategy.closeBranchTrue();
                    strategy.openBranchFalse();
                    
                    strategy.generateAction( flowFalseExitState );
                    
                    strategy.closeBranchFalse();
                }
            }
        }
    }
    
    
    private Vector identifyFilterModuleBlocks( ExecutionModel model ){
        Vector result = new Vector();
        
        Enumeration enumer = model.getEntranceStates();
        if ( !enumer.hasMoreElements() )
            return result;
        
        ExecutionState fmState = (ExecutionState) enumer.nextElement();
        while( fmState != null ){
            fmState = identifyFilterModuleBlock( fmState, result );
        }
            
        return result;
    }
    
    private ExecutionState identifyFilterModuleBlock( ExecutionState fmState, 
            Vector result )
    {
        FilterModuleBlock block = new FilterModuleBlock();
        block.fmState = fmState;
        result.add( block );
        return identifyFilterBlocks( block );
    }
    
    
    private ExecutionState identifyFilterBlocks( FilterModuleBlock fmBlock ){
        ExecutionState state = getNextState( fmBlock.fmState );
        Vector result = new Vector();
        while( state != null  &&  !isFilterModule.isTrue( state ) ){
            state = identifyFilterBlock( state, result );
        }
        
        fmBlock.filters = result;
        return state;
    }
    
    private ExecutionState identifyFilterBlock( ExecutionState startState,
            Vector result )
    {
        ExecutionState state = startState;
        while( state != null  &&  !isFilter.isTrue( state )  && 
                !isFilterModule.isTrue( state ) )
        {
            state = getNextState( state );
        }
        
        if ( !isFilter.isTrue( state ) ){
            return state;
        }
        
        FilterBlock filterBlock = new FilterBlock();
        result.add( filterBlock );
        return identifyFilterElementBlocks( state, filterBlock );        
    }
    
    
    private ExecutionState identifyFilterElementBlocks( ExecutionState filterState, 
            FilterBlock filterBlock )
    {
        ExecutionState nextFilterState = null;
        ExecutionState nextState = getNextState( filterState );
        Vector result = new Vector();
        
        while( nextState != null ){
            FilterElementBlock block = identifyFilterElementBlock( nextState );
            result.add( block );
            
            if ( isCondExpr.isTrue( block.flowTrueExitState ) ){
                nextState = block.flowTrueExitState;
            }
            else if ( isCondExpr.isTrue( block.flowFalseExitState ) ){
                nextState = block.flowFalseExitState;
            }
            else{
                nextState = null;
            }
            
            //find next filter state:
            if ( isAction.isTrue( block.flowTrueExitState ) )
            {
                ExecutionState state = getNextState( block.flowTrueExitState );
                if ( isFilter.isTrue( state )  ||  isFilterModule.isTrue( state ) ){
                    if ( nextFilterState != null  &&  nextFilterState != state )
                        throw new InlineException();
                    
                    nextFilterState = state;
                }
                else if ( isEnd.isTrue( state ) ){
                    ExecutionState state2 = getNextState( state );
                    if ( nextFilterState != null  &&  nextFilterState != state2 ){
                        throw new InlineException();
                    }
                    
                    nextFilterState = state2;
                }
            }
            
            if ( isAction.isTrue( block.flowFalseExitState ) )
            {
                ExecutionState state = getNextState( block.flowFalseExitState );
                if ( isFilter.isTrue( state )  ||  isFilterModule.isTrue( state ) ){
                    if ( nextFilterState != null  &&  nextFilterState != state )
                            throw new InlineException();
                    nextFilterState = state;
                }
                else if ( isEnd.isTrue( state ) ){
                    ExecutionState state2 = getNextState( state );
                    if ( nextFilterState != null  &&  nextFilterState != state2 )
                        throw new InlineException();
                    
                    nextFilterState = state2;
                }
            }
        }
        
        filterBlock.filterElements = result;
        return nextFilterState;
    }
    
    
    private FilterElementBlock identifyFilterElementBlock( ExecutionState condExpr ){
        FilterElementBlock block = new FilterElementBlock();
        block.conditionExprState = condExpr;
        
        Enumeration outTransitions = condExpr.getOutTransitions();
        //enumeration has 1 or 2 elements
        while( outTransitions.hasMoreElements() ){
            ExecutionTransition transition = 
                (ExecutionTransition) outTransitions.nextElement();
            
            ExecutionState exitState = getExitState( transition.getEndState() );
            
            if (transition.getLabel().equals( 
                    ExecutionLabels.CONDITION_EXPRESSION_TRUE ) )
            {
                block.flowTrueExitState = exitState;
            }
            else{
                block.flowFalseExitState = exitState;
            }
        }
        
        return block;
    }
    
    
    /**
     * Returns the first next state of the given state, or null if there is none.
     * @param state
     * @return
     */
    private ExecutionState getNextState( ExecutionState state ){
        Enumeration transitions = state.getOutTransitions();
        if ( transitions.hasMoreElements() ){
            ExecutionTransition transition = 
                (ExecutionTransition) transitions.nextElement();
            return transition.getEndState();
        }
        else{
            return null;
        }
    }
    
    
    
    /**
     * This method gets the exitstate from the filterelement for one trace
     * starting with the given state. This exitState is either an actionstate or
     * the next ConditionExpressionState, marking the next filterelement.
     * @param state
     * @return
     */
    private ExecutionState getExitState( ExecutionState state ){
        ExecutionState currentState = state;
        
        while( !isExitState( currentState ) ){
            //get the next state:
            Enumeration outTransitions = currentState.getOutTransitions();
            ExecutionTransition transition = 
                (ExecutionTransition) outTransitions.nextElement();
            currentState = transition.getEndState();
        }
        
        return currentState;
    }
    
    private boolean isExitState( ExecutionState state ){
        //exitstate is either a ConditionExpression or an Action state:
        return isCondExpr.isTrue( state )
        ||  state.getFlowNode().containsName( FlowChartNames.ACTION_NODE );
    }
    
    
    private class FilterModuleBlock{
        public ExecutionState fmState;
        public Vector filters;
        
        public FilterModuleBlock(){
        }
    }
    
    
    private class FilterBlock{
        public Vector filterElements;
        
        public FilterBlock(){
        }
    }
    
    
    private class FilterElementBlock{
        public ExecutionState conditionExprState;
        public ExecutionState flowTrueExitState;
        public ExecutionState flowFalseExitState;
        
        public FilterElementBlock(){
        }
    }
    
    
    private class ConditionalInliner{
        private HighLevelInlineStrategy strategy;
        
        private HashSet states;
        private Vector backwardStateVector;
        private Hashtable reverseTable;
        private Hashtable conditionTable;
        
        
        public ConditionalInliner( HighLevelInlineStrategy strategy ){
            this.strategy = strategy;
            
        }
        
        private void initialize( ExecutionModel model ){
            states = new HashSet();
            backwardStateVector = new Vector();
            reverseTable = new Hashtable();
            conditionTable = new Hashtable();
            
            Enumeration startStates = model.getEntranceStates();
            while( startStates.hasMoreElements() ){
                addState( (ExecutionState) startStates.nextElement() );
            }
        }
        
        private void addState( ExecutionState state ){
            if ( states.contains( state ) ){
                return;
            }

            states.add( state );
            
            reverseTable.put( state, new Vector() );

            Enumeration e = state.getOutTransitions();
            while( e.hasMoreElements() ){
                ExecutionTransition transition = (ExecutionTransition) e.nextElement();
                ExecutionState nextState = transition.getEndState();
                addState( nextState );

                Vector v = (Vector) reverseTable.get( nextState );
                v.addElement( transition );
            }

            backwardStateVector.addElement( state );
        }
        
        public void inline( ExecutionModel model, FilterModule[] modules, 
                MethodInfo method)
        {
            initialize( model );
            
            calculateConditions();
            
            startInline( model, modules, method );
        }
        
        
        private void calculateConditions(){
            ExecutionState state;
            for (int i=backwardStateVector.size()-1; i>=0; i--){
                state = (ExecutionState) backwardStateVector.elementAt( i );
                calculateCondition( state );
            }
        }
        
        
        private void calculateCondition( ExecutionState state ){
            Vector transitions = (Vector) reverseTable.get( state );
            ExecutionTransition transition;
            
            ConditionExpression currentExpr = null;
            for (int i=0; i<transitions.size(); i++){
                transition = (ExecutionTransition) transitions.elementAt( i );
                
                if ( currentExpr == null ){
                    currentExpr = getCondition( transition );
                }
                else{
                    ConditionExpression expr = getCondition( transition );
                    if ( expr != null ){
                        Or or = new Or();
                        or.setLeft( currentExpr );
                        or.setRight( expr );
                        currentExpr = or;
                    }
                }
            }
            
            if ( currentExpr != null ){
                conditionTable.put( state, currentExpr );
            }
        }
        
        
        private ConditionExpression getCondition( ExecutionTransition transition ){
            ExecutionState startState = transition.getStartState();
            FlowNode flowNode = startState.getFlowNode();
            
            //if startnode of the transition is a condition expression, add this condition
            //expression
            if ( flowNode.containsName( 
                    FlowChartNames.CONDITION_EXPRESSION_NODE ) )
            {
                ConditionExpression expr1 = 
                    (ConditionExpression) conditionTable.get( startState );
                ConditionExpression expr2 = 
                    (ConditionExpression) flowNode.getRepositoryLink();
                
                if ( transition.getLabel().equals( 
                        ExecutionLabels.CONDITION_EXPRESSION_FALSE ) )
                {
                    Not not = new Not();
                    not.setOperand( expr2 );
                    expr2 = not;
                }
                
                if ( expr2 instanceof True ){
                    return expr1;
                }
                else if ( expr1 == null ){
                    return expr2;
                }
                else{
                    And and = new And();
                    and.setLeft( expr1 );
                    and.setRight( expr2 );
                    return and;
                }
            }
            else{
                return (ConditionExpression) conditionTable.get( startState );
            }
        }
        
        
        
        
        private void startInline( ExecutionModel model, FilterModule[] modules,
                MethodInfo method )
        {
            strategy.startInline( modules, method );
            
            ExecutionState state;
            for (int i=backwardStateVector.size()-1; i>=0; i--){
                state = (ExecutionState) backwardStateVector.elementAt( i );
                if ( state.getFlowNode().containsName( FlowChartNames.ACTION_NODE ) )
                    inlineState( state );
            }
            
            strategy.endInline();
        }
        
        
        private void inlineState( ExecutionState state ){
            //don't inline continue and substitution actions:
//            FlowNode flowNode = state.getFlowNode();
//            if ( flowNode.containsName( FlowChartNames.CONTINUE_ACTION_NODE)  ||  
//                    flowNode.containsName( FlowChartNames.SUBSTITUTION_ACTION_NODE ) )
//            {
//                return;
//            }
            
            
            ConditionExpression expr = 
                (ConditionExpression) conditionTable.get( state );
            
            if ( expr != null )
                strategy.openBranchTrue( expr );
            
            strategy.generateAction( state );
            
            if ( expr != null )
                strategy.closeBranchTrue();
        }
    }
    
    
    private class InlineException extends RuntimeException{

        /**
         * 
         */
        public InlineException() {
            super();
        }
        
    }
}
