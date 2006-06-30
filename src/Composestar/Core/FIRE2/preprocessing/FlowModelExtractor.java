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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * This class extracts the flowmodel from the groove representation of the
 * flowmodel
 *
 * @author Arjan de Roo
 */
public class FlowModelExtractor {
    public final static String FLOW_NODE_ANNOTATION = "FlowNode";
    
    private final static Label FILTERMODULE_LABEL =new DefaultLabel( "FilterModule" );
    private final static Label END_LABEL = new DefaultLabel( "End" );
    
    private final static String FLOW_ELEMENT_LABEL = "FlowElement";
	private final static String CONTEXT_LABEL = "ContextNode";
	private final static String PROCEDURE_LABEL = "ProcedureNode";
	private final static String PREDICATE_LABEL = "PredicateNode";
	private final static String ACTION_LABEL = "FilterAction";
	
	private final static String FLOW_TRUE_LABEL = "flowTrue";
	private final static String FLOW_FALSE_LABEL = "flowFalse";
	private final static String FLOW_NEXT_LABEL = "flowNext";
    
    public static FlowModel extract( Graph graph ){
        FlowModel model = new FlowModel();
        
        
        Iterator iter = graph.edgeIterator();
        Edge edge;
        AnnotatedNode startNode, endNode, filterModuleNode;
        FlowNode startFlowNode, endFlowNode;
        FlowTransition transition;
        while( iter.hasNext() ){
            edge = (Edge) iter.next();
            
            String label = edge.label().text();
            int type = 0;
            if ( label.equals( FLOW_NEXT_LABEL ) ){
                type = FlowTransition.FLOW_NEXT_TRANSITION;
            }
            else if ( label.equals( FLOW_TRUE_LABEL ) ){
                type = FlowTransition.FLOW_TRUE_TRANSITION;
            }
            else if ( label.equals( FLOW_FALSE_LABEL ) ){
                type = FlowTransition.FLOW_FALSE_TRANSITION;
            }
            else{
                continue;
            }
            
            
            startNode = (AnnotatedNode) edge.source();
            endNode = (AnnotatedNode) edge.opposite();
            startFlowNode = createFlowNode( graph, startNode, model );
            endFlowNode = createFlowNode( graph, endNode, model );
            
            transition = new FlowTransition( type, startFlowNode, endFlowNode );
            
            model.addTransition( transition );
        }
        
        //startnode:
        Collection col = graph.labelEdgeSet( 2, FILTERMODULE_LABEL );
        iter = col.iterator();
        if ( !iter.hasNext() ){
            //should never happen.
            throw new RuntimeException( "FilterModule-edge not found!" );
        }
        
        edge = (Edge) iter.next();
        filterModuleNode = (AnnotatedNode) edge.source();
        model.setStartNode( 
                (FlowNode) filterModuleNode.getAnnotation( FLOW_NODE_ANNOTATION) );
        
        //endnode:
        col = graph.labelEdgeSet( 2, END_LABEL );
        iter = col.iterator();
        if ( !iter.hasNext() ){
            //should never happen.
            throw new RuntimeException( "Exit-edge not found!" );
        }
        
        edge = (Edge) iter.next();
        endNode = (AnnotatedNode) edge.source();
        model.setEndNode( 
                (FlowNode) endNode.getAnnotation( FLOW_NODE_ANNOTATION) );
        
        return model;
    }
    
    private static FlowNode createFlowNode( Graph graph, AnnotatedNode graphNode, 
            FlowModel model )
    {
        FlowNode node = (FlowNode) graphNode.getAnnotation( FLOW_NODE_ANNOTATION );
        
        if ( node != null )
            return node;
        
        RepositoryEntity entity = 
            (RepositoryEntity) graphNode.getAnnotation( "repositoryLink" );
        
        
        Collection col = graph.edgeSet( graphNode );
        Iterator iter = col.iterator();
        int type = 0;
        HashSet names = new HashSet();
        String label;
        while( iter.hasNext() ){
            Edge edge = (Edge) iter.next();
            if ( edge.source() == graphNode  &&  edge.opposite() == graphNode ){
                label = edge.label().text();
                if ( label.equals( FLOW_ELEMENT_LABEL ) ){
                    continue;
                }
                else if ( label.equals( CONTEXT_LABEL ) ){
                    type = FlowNode.CONTEXT_NODE;
                }
                else if ( label.equals( PROCEDURE_LABEL )  &&  
                        type != FlowNode.ACTION_NODE)
                {
                    type = FlowNode.PROCEDURE_NODE;
                }
                else if ( label.equals( PREDICATE_LABEL ) ){
                    type = FlowNode.PREDICATE_NODE;
                }
                else if ( label.equals( ACTION_LABEL ) ){
                    type = FlowNode.ACTION_NODE;
                }
                else{
                    names.add( label );
                }
            }
        }
        
        node = new FlowNode( type, names, entity );
        
        graphNode.addAnnotation( FLOW_NODE_ANNOTATION, node );
        
        model.addNode( node );
        
        return node;
    }
}
