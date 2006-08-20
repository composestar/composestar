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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

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
        BasicFlowModel model = new BasicFlowModel();
        
        
        Iterator iter = graph.edgeIterator();
        Edge edge;
        AnnotatedNode startNode, endNode, filterModuleNode;
        BasicFlowNode startFlowNode, endFlowNode;
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
            
            transition = new BasicFlowTransition( type, startFlowNode, endFlowNode );
            
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
    
    private static BasicFlowNode createFlowNode( Graph graph, AnnotatedNode graphNode, 
            BasicFlowModel model )
    {
        BasicFlowNode node = 
            (BasicFlowNode) graphNode.getAnnotation( FLOW_NODE_ANNOTATION );
        
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
        
        node = new BasicFlowNode( type, names, entity );
        
        graphNode.addAnnotation( FLOW_NODE_ANNOTATION, node );
        
        model.addNode( node );
        
        return node;
    }
    
    private static class BasicFlowModel extends RepositoryEntity implements FlowModel{
        private FlowNode startNode;
        private FlowNode endNode;
        private Vector nodes;
        private Vector transitions;
        
        
        public BasicFlowModel(){
            super();
            
            nodes = new Vector();
            transitions = new Vector();
        }
        
        public void addNode( FlowNode node ){
            nodes.addElement( node );
        }
        
        public void addTransition( FlowTransition transition ){
            transitions.addElement( transition );
        }
        
        
        /**
         * @return Returns the startNode.
         */
        public FlowNode getStartNode() {
            return startNode;
        }
        /**
         * @param startNode The startNode to set.
         */
        public void setStartNode(FlowNode startNode) {
            this.startNode = startNode;
        }
        
        
        /**
         * @return Returns the endNode.
         */
        public FlowNode getEndNode() {
            return endNode;
        }
        /**
         * @param endNode The endNode to set.
         */
        public void setEndNode(FlowNode endNode) {
            this.endNode = endNode;
        }
        
        public Enumeration getNodes(){
            return nodes.elements();
        }
        
        public Enumeration getTransitions(){
            return transitions.elements();
        }
    }
    
    
    private static class BasicFlowNode implements FlowNode{
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
        
        
        
        /**
         * Default constructor
         *
         */
        public BasicFlowNode( int type, HashSet names, RepositoryEntity repositoryLink ){
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
         * Returns the (first) transition from this startnode to the given endnode, or
         * null when no such transition exists.
         * 
         * @return
         */
        public FlowTransition getTransition(FlowNode endNode) {
            FlowTransition transition;
            Enumeration enumer = transitions.elements();
            
            while( enumer.hasMoreElements() ){
                transition = (FlowTransition) enumer.nextElement();
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
    
    
    private static class BasicFlowTransition implements FlowTransition{
        /**
         * The type of the transition;
         */
        private int type;
        
        
        /**
         * The startNode
         */
        private BasicFlowNode startNode;
        
        /**
         * The endNode
         */
        private BasicFlowNode endNode;

        
        /**
         * The constructor
         *
         */
        public BasicFlowTransition( int type, BasicFlowNode startNode, 
                BasicFlowNode endNode )
        {
            super();
            
            this.type = type;
            this.startNode = startNode;
            this.endNode = endNode;
            
            startNode.addTransition( this );
        }
        
        
        
        /**
         * @return Returns the type.
         */
        public int getType() {
            return type;
        }
        /**
         * @return Returns the endNode.
         */
        public FlowNode getEndNode() {
            return endNode;
        }
        
        /**
         * @return Returns the startNode.
         */
        public FlowNode getStartNode() {
            return startNode;
        }
    }
}
