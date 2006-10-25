/*
 * Created on 13-mrt-2006
 *
 */
package Composestar.Core.CORE2;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.util.iterator.ExecutionStateIterator;
import Composestar.Core.FIRE2.util.viewer.Viewer;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.SIinfo;
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
            
            if (concern.getDynObject(SIinfo.DATAMAP_KEY) != null){
                FilterModuleOrder filterModules = 
                    (FilterModuleOrder) concern.getDynObject( FilterModuleOrder.SINGLE_ORDER_KEY );
                
                check( concern, filterModules );
            }
        }
    }
    
    
    public void check( Concern concern, FilterModuleOrder modules ){
        ExecutionState state;
        ExecutionTransition transition;
        FlowNode flowNode;
        FlowTransition flowTransition;
        
        HashSet visitedNodes = new HashSet();
        HashSet visitedTransitions = new HashSet();
        Hashtable filterContinueTable = new Hashtable();
        
        FireModel fireModel = new FireModel( concern, modules, true );
        ExecutionModel execModel = fireModel.getExecutionModel();
        ExecutionStateIterator iterator = new ExecutionStateIterator( execModel );
        
        Enumeration enumer;
        
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
            
            if ( flowNode.containsName(FlowChartNames.ACTION_NODE) ){
                Filter filter = (Filter) flowNode.getRepositoryLink();
                if ( flowNode.containsName( "ContinueAction" ) ){
                    if( !filterContinueTable.containsKey( filter ) ){
                        //put boolean in the table indicating that for the
                        //filter until now only continueactions are found:
                        filterContinueTable.put( filter, Boolean.TRUE );
                    }
                }
                else{
                    //put boolean in the table indicating that for the
                    //filter not only continueactions are found:
                    filterContinueTable.put( filter, Boolean.FALSE );
                }
            }
            
            enumer = state.getOutTransitions();
            while( enumer.hasMoreElements() ){
                transition = (ExecutionTransition) enumer.nextElement();
                visitedTransitions.add( transition.getFlowTransition() );
            }
        }
        
        
        //check useless filters (filters that only continue):
        enumer = filterContinueTable.keys();
        while( enumer.hasMoreElements() ){
            Filter filter = (Filter) enumer.nextElement();
            Boolean b = (Boolean) filterContinueTable.get( filter );
            if (b.booleanValue() ){
                Debug.out( Debug.MODE_ERROR, MODULE_NAME, 
                        "Redundant filter found!", filter );
            }
        }
        
        
        FlowModel[] flowModels = fireModel.getFlowModels();
        for (int i=0; i<flowModels.length; i++){
            
            //unreachable matchingparts:
            enumer = flowModels[i].getNodes();
            while( enumer.hasMoreElements() ){
                flowNode = (FlowNode) enumer.nextElement();
                
                if ( !visitedNodes.contains( flowNode )  &&  
                        flowNode.containsName( "MatchingPart" ) )
                {
                    Debug.out( Debug.MODE_ERROR, MODULE_NAME, 
                            "Unreachable matchingpart found!",
                            flowNode.getRepositoryLink() );
                }
            }
            
            //matchingparts that always accept or reject:
            enumer = flowModels[i].getTransitions();
            while( enumer.hasMoreElements() ){
                flowTransition = (FlowTransition) enumer.nextElement();
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
                        else if ( !part.getSelector().getName().equals( "*" )  ||
                                    !part.getTarget().getName().equals( "*" ) )
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
