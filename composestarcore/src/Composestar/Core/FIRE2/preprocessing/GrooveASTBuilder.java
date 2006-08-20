/*
 * Created on 21-feb-2006
 *
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.DefaultGraph;
import groove.graph.Graph;
import groove.graph.Node;

import java.util.Hashtable;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CORfilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.DisableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperatorType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.NameMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SEQfilterCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.VoidFilterCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.VoidFilterElementCompOper;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.Message;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class GrooveASTBuilder {
    private Hashtable selectorTable;
    private Hashtable targetTable;
    
    public final static String SELECTOR_ANNOTATION = "selector";
    public final static String TARGET_ANNOTATION = "target";
    public final static String REPOSITORY_LINK_ANNOTATION = "repositoryLink";
    public final static String ACTION_NODE_ANNOTATION = "actionNode";
    
    public final static int ACCEPT_ACTION = 1;
    public final static int REJECT_ACTION = 2;
    
    public final static String STAR_REPRESENTATION = "'*'";
    
    
    
    public GrooveASTBuilder(){
        
    }
    
    public Graph buildAST( FilterModule filterModule ){
        selectorTable = new Hashtable();
        targetTable = new Hashtable();

        Graph graph = new DefaultGraph();
        
        

        AnnotatedNode filterModuleNode = new AnnotatedNode();
        filterModuleNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filterModule );
        graph.addNode( filterModuleNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( filterModuleNode, 
                FlowChartNames.FILTER_MODULE_NODE, filterModuleNode );
        graph.addEdge( edge );
        
        
        //iterate over filters:
        Vector inputFilters = filterModule.inputFilters;
        Filter filter;
        Node filterNode;
        AnnotatedNode operatorNode = null;
        for (int i=0; i<inputFilters.size(); i++){
            filter = (Filter) inputFilters.elementAt( i );
            
            //create filternode:
            filterNode = buildFilterNode( filter, graph );
            //filterNode already added to graph by the buildFilterNode method!
            
            edge = new AnnotatedEdge( filterModuleNode, FlowChartNames.FILTER_EDGE, 
                    filterNode );
            graph.addEdge( edge );
            
            if ( i==0 ){
                //create 'orderFirst' edge:
                edge = new AnnotatedEdge( filterModuleNode, 
                        FlowChartNames.ORDER_FIRST_EDGE, filterNode );
                graph.addEdge( edge );
            }
            
            //create rightArg of operatorNode:
            if ( operatorNode != null ){
                edge = new AnnotatedEdge( operatorNode, 
                        FlowChartNames.RIGHT_ARGUMENT_EDGE, filterNode );
                graph.addEdge( edge );
            }
            
            //create new operatorNode:
            operatorNode = buildFilterOperatorNode(
                    filter.rightOperator, graph );
            edge = new AnnotatedEdge( filterNode, FlowChartNames.RIGHT_OPERATOR_EDGE, 
                    operatorNode );
            graph.addEdge( edge );
            
        }
        
        
        //endnode:
        AnnotatedNode endNode = new AnnotatedNode();
        endNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filterModule );
        graph.addNode( endNode );
        
        edge = new AnnotatedEdge( endNode, FlowChartNames.END_NODE, endNode );
        graph.addEdge( edge );
        
        edge = new AnnotatedEdge( endNode, FlowChartNames.FLOW_ELEMENT_NODE, endNode );
        graph.addEdge( edge );
        
        //stopNode:
        AnnotatedNode stopNode = new AnnotatedNode();
        stopNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filterModule );
        graph.addNode( stopNode );
        
        edge = new AnnotatedEdge( stopNode, FlowChartNames.STOP_NODE, stopNode );
        graph.addEdge( edge );
        
        edge = new AnnotatedEdge( stopNode, FlowChartNames.FLOW_ELEMENT_NODE,
                stopNode );
        graph.addEdge( edge );
        
        graph.setFixed();
        
        return graph;
    }

    /**
     * @param rightOperator
     * @param graph
     * @return
     */
    private AnnotatedNode buildFilterOperatorNode(
            FilterCompOper operator, Graph graph)
    {
        if ( operator instanceof SEQfilterCompOper ){
            AnnotatedNode operatorNode = new AnnotatedNode();
            operatorNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, operator );
            graph.addNode( operatorNode );
            
            AnnotatedEdge edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.FILTER_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.SEQ_FILTER_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            return operatorNode;
        }
        else if ( operator instanceof VoidFilterCompOper ){
            AnnotatedNode operatorNode = new AnnotatedNode();
            operatorNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, operator );
            graph.addNode( operatorNode );
            
            AnnotatedEdge edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.FILTER_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.VOID_FILTER_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            return operatorNode;
        }
        else{
            //should never happen, because all cases should be handled by 
            //previous if/elses
            throw new RuntimeException( "Unknown rightOperator type" );
        }
    }

    /**
     * @param filter
     * @return
     */
    private Node buildFilterNode(Filter filter, Graph graph) {
        AnnotatedNode filterNode = new AnnotatedNode();
        filterNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filter );
        graph.addNode( filterNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( filterNode, 
                FlowChartNames.FILTER_NODE, filterNode );
        graph.addEdge( edge );
        
        //create reject and accept nodes:
        AnnotatedNode rejectNode = new AnnotatedNode();
        rejectNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filter );
        rejectNode.addAnnotation( ACTION_NODE_ANNOTATION, new Integer( REJECT_ACTION ) );
        graph.addNode( rejectNode );
        edge = new AnnotatedEdge( filterNode, FlowChartNames.REJECT_EDGE, rejectNode );
        graph.addEdge( edge );
        edge = new AnnotatedEdge( rejectNode, FlowChartNames.FILTER_ACTION_NODE,
                rejectNode );
        graph.addEdge( edge );
        
        AnnotatedNode acceptNode = new AnnotatedNode();
        acceptNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filter );
        acceptNode.addAnnotation( ACTION_NODE_ANNOTATION, new Integer( ACCEPT_ACTION ) );
        graph.addNode( acceptNode );
        edge = new AnnotatedEdge( filterNode, FlowChartNames.ACCEPT_EDGE, acceptNode );
        graph.addEdge( edge );
        edge = new AnnotatedEdge( acceptNode, FlowChartNames.FILTER_ACTION_NODE,
                acceptNode );
        graph.addEdge( edge );
        
        //add correct action to reject and accept node:
        FilterType filterType = filter.getFilterType();
        if ( filterType.type == FilterType.META ){
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.META_ACTION_NODE, 
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.ERROR ){
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.ERROR_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.DISPATCH ){
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.DISPATCH_ACTION_NODE, 
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.SEND ){
//          edge = new AnnotatedEdge( acceptNode, "SendAction", acceptNode );
            //not implemented in the groove model yet;
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.SUBSTITUTION ){
            edge = new AnnotatedEdge( acceptNode, 
                    FlowChartNames.SUBSTITUTION_ACTION_NODE, acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.WAIT ){
//          edge = new AnnotatedEdge( acceptNode, "WaitAction", acceptNode );
            //not implemented in the groove model yet;
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.APPEND ){
            //not implemented in the groove model yet;
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.PREPEND ){
            //not implemented in the groove model yet;
            edge = new AnnotatedEdge( acceptNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else if ( filterType.type == FilterType.CUSTOM ){
            edge = new AnnotatedEdge( acceptNode, 
                    FlowChartNames.CUSTOM_CONTINUE_ACTION, acceptNode );
            graph.addEdge( edge );
            edge = new AnnotatedEdge( rejectNode, FlowChartNames.CONTINUE_ACTION_NODE,
                    rejectNode );
            graph.addEdge( edge );
        }
        else{
            //should never happen, because all cases should be handled by 
            //previous if/elses
            throw new RuntimeException( "Unknown filtertype" );
        }
        
        
        
        //iterate over filterelements:
        Vector filterElements = filter.filterElements;
        FilterElement filterElement;
        Node filterElementNode;
        AnnotatedNode operatorNode = null;
        for (int i=0; i<filterElements.size(); i++){
            filterElement = (FilterElement) filterElements.elementAt( i );
            
            //create filternode:
            filterElementNode = buildFilterElementNode( filterElement, graph );
            //filterElementNode already added to graph by the buildFilterElementNode method!
            
            edge = new AnnotatedEdge( filterNode, FlowChartNames.FILTER_ELEMENT_EDGE,
                    filterElementNode );
            graph.addEdge( edge );
            
            if ( i==0 ){
                //create 'orderFirst' edge:
                edge = new AnnotatedEdge( filterNode, FlowChartNames.ORDER_FIRST_EDGE,
                        filterElementNode );
                graph.addEdge( edge );
            }
            
            //create rightArg of operatorNode:
            if ( operatorNode != null ){
                edge = new AnnotatedEdge( operatorNode, 
                        FlowChartNames.RIGHT_ARGUMENT_EDGE, filterElementNode );
                graph.addEdge( edge );
            }
            
            //create new operatorNode:
            FilterElementCompOper oper = filterElement.rightOperator;
            operatorNode = BuildFilterElementOperatorNode( oper, graph );
            edge = new AnnotatedEdge( filterElementNode, 
                    FlowChartNames.RIGHT_OPERATOR_EDGE, operatorNode );
            graph.addEdge( edge );
        }
        
        return filterNode;
    }

    /**
     * @param operator
     * @param graph
     * @return
     */
    private AnnotatedNode BuildFilterElementOperatorNode(
            FilterElementCompOper operator, Graph graph)
    {   
        if ( operator instanceof CORfilterElementCompOper ){
            AnnotatedNode operatorNode = new AnnotatedNode();
            operatorNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, operator );
            graph.addNode( operatorNode );
            
            AnnotatedEdge edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.FE_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.FE_COR_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            return operatorNode;
        }
        else if ( operator instanceof VoidFilterElementCompOper ){
            AnnotatedNode operatorNode = new AnnotatedNode();
            operatorNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, operator );
            graph.addNode( operatorNode );
            
            AnnotatedEdge edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.FE_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            edge = new AnnotatedEdge( operatorNode, 
                    FlowChartNames.FE_VOID_COMP_OPER_NODE, operatorNode );
            graph.addEdge( edge );
            
            return operatorNode;
        }
        else{
            //should never happen, because all cases should be handled by 
            //previous if/elses
            throw new RuntimeException( "Unknown rightOperator type" );
        }
    }

    /**
     * @param filterElement
     * @param graph
     * @return
     */
    private Node buildFilterElementNode(FilterElement filterElement, Graph graph) {
        AnnotatedNode filterElementNode = new AnnotatedNode();
        filterElementNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, filterElement );
        graph.addNode( filterElementNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( filterElementNode, 
                FlowChartNames.FILTER_ELEMENT_NODE, filterElementNode );
        graph.addEdge( edge );
        
        //conditionpart:
        AnnotatedNode conditionPartNode = 
            buildConditionPartNode( filterElement.conditionPart, graph );
        edge = new AnnotatedEdge( 
                filterElementNode, FlowChartNames.CONDITION_PART_EDGE, 
                conditionPartNode );
        graph.addEdge( edge );
        
        //conditionoperator:
        AnnotatedNode conditionOperatorNode =
            buildConditionOperatorNode( filterElement.enableOperatorType, graph );
        edge = new AnnotatedEdge( 
                filterElementNode, FlowChartNames.CONDITION_OPERATOR_EDGE,
                conditionOperatorNode );
        graph.addEdge( edge );
        
        //iterate over matchingpatterns::
        Vector matchingpatterns = filterElement.matchingPatterns;
        MatchingPattern pattern;
        Node patternNode;
        Node previousPatternNode = null;
        for (int i=0; i<matchingpatterns.size(); i++){
            pattern = (MatchingPattern) matchingpatterns.elementAt( i );
            
            //create patternNode:
            patternNode = buildMatchingPatternNode( pattern, graph );
            //patternNode already added to graph by the buildMatchingPatternNode method!
            
            edge = new AnnotatedEdge( filterElementNode, 
                    FlowChartNames.MATCHING_PATTERN_EDGE, patternNode );
            graph.addEdge( edge );
            
            if ( i==0 ){
                //create 'orderFirst' edge:
                edge = new AnnotatedEdge( filterElementNode, 
                        FlowChartNames.ORDER_FIRST_EDGE, patternNode );
                graph.addEdge( edge );
            }
            else{
                //create 'orderNext' edge:
                edge = new AnnotatedEdge( previousPatternNode, 
                        FlowChartNames.ORDER_NEXT_EDGE, patternNode );
                graph.addEdge( edge );
            }
            
            previousPatternNode = patternNode;
        }
        
        return filterElementNode;
    }

    /**
     * @param pattern
     * @param graph
     * @return
     */
    private Node buildMatchingPatternNode(
            MatchingPattern pattern, Graph graph)
    {
        AnnotatedNode patternNode = new AnnotatedNode();
        patternNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, pattern );
        graph.addNode( patternNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                patternNode, FlowChartNames.MATCHING_PATTERN_NODE, patternNode );
        graph.addEdge( edge );
        
        //matchingpart:
        MatchingPart matchingPart = 
            (MatchingPart) pattern.matchingParts.elementAt( 0 );
        Node matchingPartNode = buildMatchingPartNode( matchingPart, graph );
        edge = new AnnotatedEdge( patternNode, FlowChartNames.MATCHING_PART_EDGE,
                matchingPartNode );
        graph.addEdge( edge );
        
        //substitutionpart:
        Node substitutionPartNode;
        SubstitutionPart substitutionPart = 
            (pattern.substitutionParts.size() == 0) ? null :
                (SubstitutionPart) pattern.substitutionParts.elementAt(0);
        
        substitutionPartNode = buildSubstitutionPartNode(
                substitutionPart, graph );
        
        edge = new AnnotatedEdge( 
                patternNode, FlowChartNames.SUBSTITUTION_PART_EDGE,
                substitutionPartNode );
        graph.addEdge( edge );
        
        return patternNode;
    }

    /**
     * @param substitutionPart
     * @param graph
     * @return
     */
    private Node buildSubstitutionPartNode(MatchingPart substitutionPart, Graph graph) {
        AnnotatedNode substitutionPartNode = new AnnotatedNode();
        substitutionPartNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, substitutionPart );
        graph.addNode( substitutionPartNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                substitutionPartNode, FlowChartNames.SUBSTITUTION_PART_NODE,
                substitutionPartNode );
        graph.addEdge( edge );
        
        //selector:
        MessageSelector selector = substitutionPart.selector;
        Node selectorNode = (Node) selectorTable.get( selector.name );
        if ( selectorNode == null ){
            selectorNode = buildSelectorNode( selector, graph );
        }
        
        edge = new AnnotatedEdge( substitutionPartNode, FlowChartNames.SELECTOR_EDGE,
                selectorNode );
        graph.addEdge( edge );
        
        //target:
        Target target = substitutionPart.target;
        Node targetNode = (Node) targetTable.get( target.name );
        if ( targetNode == null ){
            targetNode = buildTargetNode( target, graph );
        }
        
        edge = new AnnotatedEdge( substitutionPartNode, FlowChartNames.TARGET_EDGE,
                targetNode );
        graph.addEdge( edge );
        
        return substitutionPartNode;
    }
    
    /**
     * @param substitutionPart
     * @param graph
     * @return
     */
    private Node buildSubstitutionPartNode(SubstitutionPart substitutionPart, Graph graph) {
        AnnotatedNode substitutionPartNode = new AnnotatedNode();
        substitutionPartNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, substitutionPart );
        graph.addNode( substitutionPartNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                substitutionPartNode, FlowChartNames.SUBSTITUTION_PART_NODE,
                substitutionPartNode );
        graph.addEdge( edge );
        
        //selector:
        MessageSelector selector;
        if ( substitutionPart == null )
            selector = Message.STAR_SELECTOR;
        else
            selector = substitutionPart.selector;
        Node selectorNode = (Node) selectorTable.get( selector.name );
        if ( selectorNode == null ){
            selectorNode = buildSelectorNode( selector, graph );
        }
        
        edge = new AnnotatedEdge( substitutionPartNode, FlowChartNames.SELECTOR_EDGE,
                selectorNode );
        graph.addEdge( edge );
        
        //target:
        Target target;
        if ( substitutionPart == null )
            target = Message.STAR_TARGET;
        else
            target = substitutionPart.target;
        Node targetNode = (Node) targetTable.get( target.name );
        if ( targetNode == null ){
            targetNode = buildTargetNode( target, graph );
        }
        
        edge = new AnnotatedEdge( substitutionPartNode, FlowChartNames.TARGET_EDGE,
                targetNode );
        graph.addEdge( edge );
        
        return substitutionPartNode;
    }

    /**
     * @param matchingPart
     * @param graph
     * @return
     */
    private Node buildMatchingPartNode(MatchingPart matchingPart, Graph graph) {
        AnnotatedNode matchingPartNode = new AnnotatedNode();
        matchingPartNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, matchingPart );
        graph.addNode( matchingPartNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                matchingPartNode, FlowChartNames.MATCHING_PART_NODE,
                matchingPartNode );
        graph.addEdge( edge );
        
        if ( matchingPart.matchType instanceof NameMatchingType ){
            edge = new AnnotatedEdge( 
                    matchingPartNode, FlowChartNames.NAME_MATCHING_NODE, 
                    matchingPartNode );
            graph.addEdge( edge );
        }
        else{
            edge = new AnnotatedEdge( 
                    matchingPartNode, FlowChartNames.SIGNATURE_MATCHING_NODE, 
                    matchingPartNode );
            graph.addEdge( edge );
        }
        
        //selector:
        MessageSelector selector = matchingPart.selector;
        Node selectorNode = (Node) selectorTable.get( selector.name );
        if ( selectorNode == null ){
            selectorNode = buildSelectorNode( selector, graph );
        }
        
        edge = new AnnotatedEdge( matchingPartNode, FlowChartNames.SELECTOR_EDGE,
                selectorNode );
        graph.addEdge( edge );
        
        //target:
        Target target = matchingPart.target;
        Node targetNode = (Node) targetTable.get( target.name );
        if ( targetNode == null ){
            targetNode = buildTargetNode( target, graph );
        }
        
        edge = new AnnotatedEdge( matchingPartNode, FlowChartNames.TARGET_EDGE,
                targetNode );
        graph.addEdge( edge );
        
        return matchingPartNode;
    }

    /**
     * @param target
     * @param graph
     * @return
     */
    private Node buildTargetNode(Target target, Graph graph) {
        AnnotatedNode targetNode = new AnnotatedNode();
        targetNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, target );
        
        String name = target.getName();
        if ( name.equals( "*" ) ){
            name = "'*'";
        }
        
        targetNode.addAnnotation( TARGET_ANNOTATION, name );
        graph.addNode( targetNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                targetNode, name, targetNode );
        graph.addEdge( edge );
        
        
        targetTable.put( name, targetNode );
        
        return targetNode;
    }

    /**
     * @param selector
     * @param graph
     * @return
     */
    private Node buildSelectorNode(MessageSelector selector, Graph graph) {
        AnnotatedNode selectorNode = new AnnotatedNode();
        selectorNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, selector );
        String name = selector.getName();
        if ( name.equals( "*" ) ){
            name = "'*'";
        }
        selectorNode.addAnnotation( SELECTOR_ANNOTATION, name );
        graph.addNode( selectorNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                selectorNode, name, selectorNode );
        graph.addEdge( edge );
        
        selectorTable.put( name, selectorNode );
        
        return selectorNode;
    }

    /**
     * @param enableOperatorType
     * @param graph
     * @return
     */
    private AnnotatedNode buildConditionOperatorNode(
            EnableOperatorType enableOperatorType, Graph graph)
    {
        AnnotatedNode operatorNode = new AnnotatedNode();
        operatorNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, enableOperatorType );
        graph.addNode( operatorNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                operatorNode, FlowChartNames.CONDITION_OPERATOR_NODE, operatorNode );
        graph.addEdge( edge );
        
        if ( enableOperatorType instanceof DisableOperator ){
            edge = new AnnotatedEdge( 
                    operatorNode, FlowChartNames.DISABLE_OPERATOR_NODE, operatorNode );
            graph.addEdge( edge );
        }
        else if ( enableOperatorType instanceof EnableOperator ){
            edge = new AnnotatedEdge( 
                    operatorNode, FlowChartNames.ENABLE_OPERATOR_NODE, operatorNode );
            graph.addEdge( edge );
        }
        else{
            //should never happen, because all cases should be handled by 
            //previous if/elses
            throw new RuntimeException( "Unknown EnableOperatorType" );
        }
        
        return operatorNode;
    }

    /**
     * @param conditionExpression
     * @param graph
     * @return
     */
    private AnnotatedNode buildConditionPartNode(
            ConditionExpression conditionExpression, Graph graph)
    {
        AnnotatedNode conditionNode = new AnnotatedNode();
        conditionNode.addAnnotation( REPOSITORY_LINK_ANNOTATION, conditionExpression );
        graph.addNode( conditionNode );
        
        AnnotatedEdge edge = new AnnotatedEdge( 
                conditionNode, FlowChartNames.CONDITION_EXPRESSION_NODE, conditionNode );
        graph.addEdge( edge );
        
        //if always true or false, add this information to the graph:
        if ( conditionExpression instanceof True ){
            edge = new AnnotatedEdge( 
                    conditionNode, FlowChartNames.TRUE_NODE, conditionNode );
            graph.addEdge( edge );
        }
        else if ( conditionExpression instanceof False ){
            edge = new AnnotatedEdge( 
                    conditionNode, FlowChartNames.FALSE_NODE, conditionNode );
            graph.addEdge( edge );
        }
        
        
        return conditionNode;
    }
}
