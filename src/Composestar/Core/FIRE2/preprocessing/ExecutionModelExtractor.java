/*
 * Created on 22-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Graph;
import groove.graph.Label;
import groove.graph.Node;
import groove.lts.DefaultGraphState;
import groove.lts.DefaultGraphTransition;
import groove.lts.GTS;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.model.Message;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class ExecutionModelExtractor {
    private Hashtable stateTable;
    private final static Label PC_LABEL = new DefaultLabel( "pc" );
    private final static Label SELECTOR_LABEL = new DefaultLabel( "selector" );
    private final static Label TARGET_LABEL = new DefaultLabel( "target" );
    private final static Label SUBSTITUTIONSELECTOR_LABEL = 
        new DefaultLabel( "substitutionSelector" );
    private final static Label SUBSTITUTIONTARGET_LABEL = 
        new DefaultLabel( "substitutionTarget" );
    
    public ExecutionModelExtractor(){
    }
    
    public ExecutionModel extract( GTS gts, FlowModel flowModel ){
        stateTable = new Hashtable();
        
        ExecutionModel executionModel = new ExecutionModel();
        
        DefaultGraphState startState = (DefaultGraphState) gts.startState();
        Iterator iter = startState.getOutTransitionIter();
        DefaultGraphTransition transition;
        DefaultGraphState nextState;
        
        while( iter.hasNext() ){
            transition = (DefaultGraphTransition) iter.next();
            nextState = (DefaultGraphState) transition.target();
            
            if ( !stateTable.containsKey( nextState ) ){
                addState( nextState, executionModel, flowModel );
                analyseState( nextState, executionModel, flowModel );
            }
        }
        
//        ExecutionState state = new ExecutionState( null, "" );
//        stateTable.put( startState, state );
//        analyseNextStates( startState, model );
//        
        
        return executionModel;
    }
    
    private void analyseState( DefaultGraphState state, ExecutionModel executionModel,
            FlowModel flowModel )
    {
        Iterator iter = state.getOutTransitionIter();
        
        DefaultGraphTransition transition;
        DefaultGraphState nextState;
        ExecutionState startState, endState;
        
        while( iter.hasNext() ){
            transition = (DefaultGraphTransition) iter.next();
            nextState = (DefaultGraphState) transition.target();
            if ( !stateTable.containsKey( nextState ) ){
                addState( nextState, executionModel, flowModel );
                analyseState( nextState, executionModel, flowModel );
            }
            
            startState = (ExecutionState) stateTable.get( state );
            endState = (ExecutionState) stateTable.get( nextState );
            
            addTransition( startState, endState, transition, executionModel );
        }
    }
    
    private void addTransition( ExecutionState startState, ExecutionState endState,
            DefaultGraphTransition transition, ExecutionModel executionModel )
    {
        FlowTransition flowTransition = 
            startState.getFlowNode().getTransition( endState.getFlowNode() );
        
        ExecutionTransition exeTrans = new ExecutionTransition(
                startState, transition.label().text(), endState, flowTransition );
        
        executionModel.addTransition( exeTrans );
    }
    
    private void addState( DefaultGraphState state, ExecutionModel executionModel,
            FlowModel flowModel )
    {
        Node selectorNode = null;
        Node targetNode = null;
        Node substitutionSelectorNode = null;
        Node substitutionTargetNode = null;
        
        MessageSelector selector, substitutionSelector;
        Target target, substitutionTarget;
        
        
        Graph graph = state.getGraph();
        Collection pcEdges = graph.labelEdgeSet( 2, PC_LABEL );
        Iterator iter = pcEdges.iterator();
        if ( !iter.hasNext() ){
            //should never happen.
            throw new RuntimeException( "pc-edge not found!" );
        }
        
        Edge edge = (Edge) iter.next();
        
        //FlowNode:
        AnnotatedNode targetFlowNode = (AnnotatedNode) edge.opposite();
        FlowNode flowNode = (FlowNode) targetFlowNode.getAnnotation( 
                FlowModelExtractor.FLOW_NODE_ANNOTATION );
        if ( flowNode == null ){
            //should never happen.
            throw new RuntimeException( "FlowNode not found!" );
        }
        
        //selector, target, substitutionSelector and substitutionTarget:
        iter = graph.outEdgeSet( edge.source() ).iterator();
        while( iter.hasNext() ){
            edge = (Edge) iter.next();
            if ( edge.label().equals( SELECTOR_LABEL ) ){
                selectorNode = edge.opposite();
            }
            else if ( edge.label().equals( TARGET_LABEL ) ){
                targetNode = edge.opposite();
            }
            else if ( edge.label().equals( SUBSTITUTIONSELECTOR_LABEL ) ){
                substitutionSelectorNode = edge.opposite();
            }
            else if ( edge.label().equals( SUBSTITUTIONTARGET_LABEL ) ){
                substitutionTargetNode = edge.opposite();
            }
        }
        
        if ( selectorNode == null ){
            //should never happen
            throw new RuntimeException( "No selector!" );
        }
        if ( targetNode == null ){
            //should never happen
            throw new RuntimeException( "No target!" );
        }
        
        
        if ( selectorNode instanceof AnnotatedNode ){
            selector = (MessageSelector) ((AnnotatedNode) selectorNode).getAnnotation( 
                    GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION );
        }
        else{
            selector = Message.STAR_SELECTOR;
        }
        
        if ( targetNode instanceof AnnotatedNode ){
            target = (Target) ((AnnotatedNode) targetNode).getAnnotation( 
                    GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION );
        }
        else{
            target = Message.STAR_TARGET;
        }
        
        if ( substitutionSelectorNode != null  &&
                substitutionSelectorNode instanceof AnnotatedNode )
        {
            substitutionSelector = 
                (MessageSelector) 
                ((AnnotatedNode) substitutionSelectorNode).getAnnotation( 
                    GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION );
        }
        else{
            substitutionSelector = Message.STAR_SELECTOR;
        }
        
        if ( substitutionTargetNode != null  &&
                substitutionTargetNode instanceof AnnotatedNode )
        {
            substitutionTarget = 
                (Target) ((AnnotatedNode) substitutionTargetNode).getAnnotation( 
                    GrooveASTBuilder.REPOSITORY_LINK_ANNOTATION );
        }
        else{
            substitutionTarget = Message.STAR_TARGET;
        }
        
        
        ExecutionState executionState;
        
        //check for start- or endnode:
        if ( flowNode == flowModel.getStartNode() ){
            executionState = new ExecutionState( flowNode, selector, target,
                    substitutionSelector, substitutionTarget,
                    ExecutionState.ENTRANCE_STATE );
            executionModel.addState( executionState );
            executionModel.addEntranceState( executionState );
        }
        else if ( flowNode == flowModel.getEndNode() ){
            executionState = new ExecutionState( flowNode, selector, target,
                    substitutionSelector, substitutionTarget,
                    ExecutionState.EXIT_STATE );
            executionModel.addState( executionState );
            executionModel.addExitState( executionState );
        }
        else{
            executionState = new ExecutionState( flowNode, selector, target,
                    substitutionSelector, substitutionTarget,
                    ExecutionState.NORMAL_STATE );
            executionModel.addState( executionState );
        }
        
        stateTable.put( state, executionState );
    }
}
