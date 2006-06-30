/*
 * Created on 13-mrt-2006
 *
 */
package Composestar.Core.CORE2;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.analysis.DepthFirstIterator;
import Composestar.Core.FIRE2.analysis.FireAnalysis;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class Core implements CTCommonModule{
    private final static String MODULE_NAME = "CORE";
    
    
    public Core(){
    }
    
    
    public void run(CommonResources resources) throws ModuleException {
        Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
        while( conIter.hasNext() )
        {
            Concern concern = (Concern) conIter.next();
            
            if (concern.getDynObject("superImpInfo") != null){
                FilterModuleOrder filterModules = 
                    (FilterModuleOrder) concern.getDynObject( "SingleOrder" );
                
                Vector order = filterModules._order;
                FilterModule[] modules = new FilterModule[ order.size() ];
                for (int i=0; i<order.size(); i++){
                    String ref = (String) order.elementAt( i );
                    
                    modules[i] = 
                        (FilterModule) DataStore.instance().getObjectByID(ref);
                }
                
                check( concern, modules );
            }
        }
    }
    
    
    public void check( Concern concern, FilterModule[] modules ){
        ExecutionState state;
        ExecutionTransition transition;
        FlowNode flowNode;
        FlowTransition flowTransition;
        
        HashSet visitedNodes = new HashSet();
        HashSet visitedTransitions = new HashSet();
        Hashtable filterContinueTable = new Hashtable();
        
        FireAnalysis model = new FireAnalysis( concern, modules );
        DepthFirstIterator iterator = new DepthFirstIterator( model );
        
        Enumeration enum;
        
        Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Checking concern:   o/ ");
        if ( concern != null ){
            Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Checking concern:  /|   ... " + 
                    concern.getName()+ " ...");
        }
		Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Checking concern:  / \\ ");
        
        while( iterator.hasNext() ){
            state = (ExecutionState) iterator.next();
            flowNode = state.getFlowNode();
            visitedNodes.add( flowNode );
            
            if ( flowNode.getType() == FlowNode.ACTION_NODE ){
                Filter filter = (Filter) flowNode.getRepositoryLink();
                if ( flowNode.containsName( "ContinueAction" ) ){
                    if( !filterContinueTable.containsKey( filter ) ){
                        //put boolean in the table indicating that for the
                        //filter until now only continueactions are found:
                        filterContinueTable.put( filter, new Boolean( true ) );
                    }
                }
                else{
                    //put boolean in the table indicating that for the
                    //filter not only continueactions are found:
                    filterContinueTable.put( filter, new Boolean( false ) );
                }
            }
            
            enum = state.getOutTransitions();
            while( enum.hasMoreElements() ){
                transition = (ExecutionTransition) enum.nextElement();
                visitedTransitions.add( transition.getFlowTransition() );
            }
        }
        
        
        //check useless filters (filters that only continue):
        enum = filterContinueTable.keys();
        while( enum.hasMoreElements() ){
            Filter filter = (Filter) enum.nextElement();
            Boolean b = (Boolean) filterContinueTable.get( filter );
            if ( b.booleanValue() == true ){
                Debug.out( Debug.MODE_ERROR, MODULE_NAME, 
                        "Redundant filter found!", filter );
            }
        }
        
        
        FlowModel[] flowModels = model.getFlowModels();
        for (int i=0; i<flowModels.length; i++){
            
            //unreachable matchingparts:
            enum = flowModels[i].getNodes();
            while( enum.hasMoreElements() ){
                flowNode = (FlowNode) enum.nextElement();
                
                if ( !visitedNodes.contains( flowNode )  &&  
                        flowNode.containsName( "MatchingPart" ) )
                {
                    Debug.out( Debug.MODE_ERROR, MODULE_NAME, 
                            "Unreachable matchingpart found!",
                            flowNode.getRepositoryLink() );
                }
            }
            
            //matchingparts that always accept or reject:
            enum = flowModels[i].getTransitions();
            while( enum.hasMoreElements() ){
                flowTransition = (FlowTransition) enum.nextElement();
                if ( !visitedTransitions.contains( flowTransition ) )
                {
                    FlowNode startNode = flowTransition.getStartNode();
                    if ( visitedNodes.contains( startNode )  &&
                            startNode.containsName( "MatchingPart") )
                    {
                        MatchingPart part = 
                            (MatchingPart) startNode.getRepositoryLink();
                        if ( flowTransition.getType() == 
                            FlowTransition.FLOW_TRUE_TRANSITION )
                        {
                            Debug.out( Debug.MODE_WARNING, MODULE_NAME, 
                                    "Matchingpart never accepts!", 
                                    startNode.getRepositoryLink() );
                        }
                        else if ( !part.selector.getName().equals( "*" )  ||
                                    !part.target.getName().equals( "*" ) )
                        {
                            Debug.out( Debug.MODE_WARNING, MODULE_NAME, 
                                    "Matchingpart always accepts!", 
                                    startNode.getRepositoryLink() );
                        }
                        
                    }
                }
            }
        }
    }
}
