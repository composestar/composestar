/*
 * Created on 9-mrt-2006
 *
 */
package Composestar.Core.FIRE2.model;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.FIRE2.preprocessing.Preprocessor;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class FireModel {
    private Concern concern;
    private FlowModel[] flowModels;
    private ExecutionModel[] executionModels;
    private FilterModule[] filterModules;
    
    
    public final static int NO_SIGNATURE_CHECK = 0;
    public final static int LOOSE_SIGNATURE_CHECK = 1;
    public final static int STRICT_SIGNATURE_CHECK = 2;
    
    private final static int SIGNATURE_MATCH_TRUE = 1;
    private final static int SIGNATURE_MATCH_FALSE = 2;
    private final static int SIGNATURE_MATCH_UNKNOWN = 3;
    
    
    public FireModel( Concern concern, FilterModuleOrder order ){
        this.concern = concern;
        
        Vector v = order._order;
        
        FilterModule[] modules = new FilterModule[ v.size() ];
        for (int i=0; i<v.size(); i++){
            String ref = (String) v.elementAt( i );
            
            modules[i] = 
                (FilterModule) DataStore.instance().getObjectByID(ref);
        }
        
        initialize( modules );
    }
    
    public FireModel( Concern concern, FilterModule[] modules ){
        this.concern = concern;
        
        initialize( modules );
    }
    

    
    private void initialize( FilterModule[] modules ){
        this.filterModules = modules;
        this.flowModels = new FlowModel[ modules.length ];
        this.executionModels = new ExecutionModel[ modules.length ];
        
        FirePreprocessingResult result;
        for (int i=0; i<modules.length; i++){
            result = (FirePreprocessingResult) modules[i].getDynObject( 
                    Preprocessor.RESULT_ID );
            
            flowModels[i] = result.getFlowModel();
            
            executionModels[i] = result.getExecutionModel();
        }
    }
    
    
    
    /**
     * @return Returns the executionModels.
     */
    public ExecutionModel[] getExecutionModels() {
        return executionModels;
    }
    /**
     * @return Returns the flowModels.
     */
    public FlowModel[] getFlowModels() {
        return flowModels;
    }
    
    /**
     * @return the filterModules
     */
    public FilterModule[] getFilterModules() {
        return filterModules;
    }

    
    private Vector getOutTransitions( ExtendedExecutionState state )
    {
        if ( state.getStateType() == ExecutionState.EXIT_STATE )
            return getOutTransitionsCrossLayer( state );
        else
            return getOutTransitionsCurrentLayer( state );
    }
    
    private Vector getOutTransitionsCurrentLayer( ExtendedExecutionState state ){
        ExecutionTransition transition;
        int signatureCheck = state.signatureCheck;
        MethodInfo methodInfo = state.signatureCheckInfo;
        ExecutionState baseState = state.baseState;
        Enumeration baseEnum;
        
        if ( signatureCheck != NO_SIGNATURE_CHECK  &&  
                state.getFlowNode().containsName(
                        FlowChartNames.SIGNATURE_MATCHING_NODE) )
        {
            int result = signatureCheck( state, signatureCheck, methodInfo );
            if ( result == SIGNATURE_MATCH_UNKNOWN )
            {
                if ( signatureCheck == STRICT_SIGNATURE_CHECK ){
                    Vector v = new Vector();
                    baseEnum = v.elements();
                }
                else
                    baseEnum = baseState.getOutTransitions();
            }
            else if ( result == SIGNATURE_MATCH_TRUE ){
                Vector v = new Vector();
                Enumeration enumer = baseState.getOutTransitions();
                while( enumer.hasMoreElements() ){
                    transition = (ExecutionTransition) enumer.nextElement();
                    if ( transition.getFlowTransition().getType() == 
                        FlowTransition.FLOW_TRUE_TRANSITION )
                    {
                        v.addElement( transition );
                    }
                }
                baseEnum = v.elements();
            }
            else{
                Vector v = new Vector();
                Enumeration enumer = baseState.getOutTransitions();
                while( enumer.hasMoreElements() ){
                    transition = (ExecutionTransition) enumer.nextElement();
                    if ( transition.getFlowTransition().getType() == 
                        FlowTransition.FLOW_FALSE_TRANSITION )
                    {
                        v.addElement( transition );
                    }
                }
                baseEnum = v.elements();
            }
        }
        else{
            baseEnum = baseState.getOutTransitions();
        }
        
        //create ExtendedExecutionTransitions:
        Vector outTransitions = new Vector();
        while( baseEnum.hasMoreElements() ){
            ExecutionTransition baseTransition = 
                (ExecutionTransition) baseEnum.nextElement();
            outTransitions.addElement( 
                    new ExtendedExecutionTransition( 
                            state.model, state, baseTransition, false ) );
        }
        
        return outTransitions;
    }
    
    private Vector getOutTransitionsCrossLayer( ExtendedExecutionState startState )
    {
        Message m;
        ExecutionState returnState;
        int layer = startState.layer;
        
        if ( layer == executionModels.length-1 ){
            return new Vector();
        }
        
        ExtendedExecutionState[] nextStates = new ExtendedExecutionState[1];
        
        ExecutionState nextState = executionModels[layer+1].getEntranceState(
                startState.getMessage() );
        
        
        nextStates[0] = deriveState( nextState, startState, layer+1 );
        
        if ( nextStates[0] == null ){
            //should not occur
            throw new RuntimeException( "No next state found, while" +
            " there should have been one!" );
        }
        
        Vector result = new Vector();
        result.addElement( new ExtendedExecutionTransition( startState, nextStates[0] ) );
        return result;
    }
    
    private int signatureCheck( ExecutionState state, int signatureCheck, 
            MethodInfo methodInfo )
    {
        //check for signaturematching:
        if ( signatureCheck != NO_SIGNATURE_CHECK  &&  
                state.getFlowNode().containsName(
                        FlowChartNames.SIGNATURE_MATCHING_NODE) )
        {
            boolean flowTrue = false;
            boolean flowFalse = false;
            
            MatchingPart matchingPart = 
                (MatchingPart) state.getFlowNode().getRepositoryLink();
            
            //get the matching target:
            Target matchTarget = matchingPart.getTarget();
            if ( Message.checkEquals( matchTarget, Message.STAR_TARGET ) )
                matchTarget = state.getMessage().getTarget();
            
            //get the matching selector:
            MessageSelector matchSelector = matchingPart.getSelector();
            if ( Message.checkEquals( matchSelector, Message.STAR_SELECTOR ) )
                matchSelector = state.getMessage().getSelector();
            
            if ( matchTarget.name.equals( "inner" ) ){
                List methods;
                Type matchType = (Type) concern.getPlatformRepresentation();
                if ( matchType == null )
                    methods = new LinkedList();
                else
                    methods = matchType.getMethods();
                
                MethodInfo matchMethodInfo = methodInfo.getClone(
                        matchSelector.getName(), matchType );
                
                if ( methods.contains( matchMethodInfo ) )
                    return SIGNATURE_MATCH_TRUE;
                else
                    return SIGNATURE_MATCH_FALSE;
            }
            else{
                DeclaredObjectReference ref = 
                    (DeclaredObjectReference) matchTarget.getRef();
                Concern matchConcern = ref.getRef().getType().getRef();
                Signature signature = matchConcern.getSignature();
                if ( signature == null )
                    signature = new Signature();
                Type matchType = (Type) matchConcern.getPlatformRepresentation();
                MethodInfo matchMethodInfo = methodInfo.getClone(
                        matchSelector.getName(), matchType );
                
                if ( !signature.hasMethod( matchMethodInfo ) )
                    return SIGNATURE_MATCH_FALSE;
                else{
                    MethodWrapper wrapper = 
                        signature.getMethodWrapper( matchMethodInfo );
                    if ( wrapper.RelationType == MethodWrapper.UNKNOWN )
                        return SIGNATURE_MATCH_UNKNOWN;
                    else
                        return SIGNATURE_MATCH_TRUE;
                }
            }
        }
        else{
            return SIGNATURE_MATCH_UNKNOWN;
        }
            
    }
    
    
    /**
     * Derives the correct state. If the oldState has not a generalized
     * message and the newstate has, the derivedState has the applied message
     * of the newState generalization to the oldState.
     * @param baseState
     * @param startState
     * @return
     */
    private ExtendedExecutionState deriveState( ExecutionState baseState, 
            ExtendedExecutionState startState, int layer )
    {
        if ( !baseState.getMessage().isGeneralization() ){
            return new ExtendedExecutionState( startState.model, baseState,
                    baseState.getMessage(), startState.signatureCheck, 
                    startState.signatureCheckInfo, layer );
        }
        else{
        
            Message message = startState.getMessage();
            Message newStateMessage = baseState.getMessage();
            Target derivedTarget = 
                ( Message.checkEquals( newStateMessage.getTarget(), 
                        Message.STAR_TARGET ) ?
                                message.getTarget() : newStateMessage.getTarget() );

            MessageSelector derivedSelector = 
                ( Message.checkEquals( newStateMessage.getSelector(), 
                        Message.STAR_SELECTOR ) ?
                                message.getSelector() : newStateMessage.getSelector() );

            Message derivedMessage = new Message( derivedTarget, derivedSelector );

            return new ExtendedExecutionState( startState.model, baseState, 
                    derivedMessage, startState.signatureCheck, 
                    startState.signatureCheckInfo, layer );
        }
    }
    
    
    /**
     * Returns the ExecutionModel for a given entranceselector.
     * @param selector
     * @return
     */
    public ExecutionModel getExecutionModel( String selector ){
        return new ExtendedExecutionModel( selector );
    }
    
    
    
    /**
     * Returns the ExecutionModel for a given methodInfo.
     * @param methodInfo The methodinfo
     * @param signatureCheck Indicates whether a signatureCheck needs to be done.
     * @return
     */
    public ExecutionModel getExecutionModel( MethodInfo methodInfo, 
            int signatureCheck )
    {
        return new ExtendedExecutionModel( methodInfo, signatureCheck );
    }
    
    /**
     * Returns the ExecutionModel for a given target and methodinfo.
     * @param target The entrance target
     * @param methodInfo The entrance method
     * @param signatureCheck Indicates whether a signatureCheck needs to be done.
     * @return
     */
    public ExecutionModel getExecutionModel( Target target, MethodInfo methodInfo,
            int signatureCheck )
    {
        return new ExtendedExecutionModel( target, methodInfo, signatureCheck );
    }
    
    
    public static Message getEntranceMessage( String selector ){
        Target t = new Target();
        t.name = "inner";
        MessageSelector s = new MessageSelector(new MessageSelectorAST());
        s.setName(selector);
        
        return new Message( t, s );
    }
    
    public ExecutionModel getExecutionModel(){
        return new ExtendedExecutionModel();
    }
    
    
    
    public HashSet getDistinguishable(){
        HashSet distinguishable = new HashSet();
        for (int i=0; i<executionModels.length; i++){
            Set selectors = executionModels[i].getEntranceMessages();
            Iterator iter = selectors.iterator();
            while( iter.hasNext() ){
                Message message = (Message) iter.next();
                if ( !Message.checkEquals( 
                        message.getSelector(), Message.STAR_SELECTOR) )
                {
                    distinguishable.add( message.getSelector().getName() );
                }
            }
        }
        
        return distinguishable;
    }
    
    
    public Target getTarget( External external ){
	//TODO
	return null;
    }
    
    public Target getTarget( Internal internal ){
	//TODO
	return null;
    }
    
    
    private class ExtendedExecutionModel implements ExecutionModel{
        private Hashtable entranceTable = new Hashtable();
        private Hashtable stateCache = new Hashtable();
        
        /**
         * Indicates whether this ExecutionModel is a full model for the
         * filterset or just the executionmodel of one entrance message.
         */
        private boolean fullModel;
        
        
        public ExtendedExecutionModel(){
            String selector;
            Message message;
            ExecutionState state;
            ExtendedExecutionState extendedState;
            
            HashSet distinguishable = getDistinguishable();
            Iterator iter = distinguishable.iterator();
            while( iter.hasNext() ){
                selector = (String) iter.next();
                message = getEntranceMessage( selector );
                
                state = executionModels[0].getEntranceState( message );
                
                extendedState =
                    new ExtendedExecutionState( this, state, message,
                            NO_SIGNATURE_CHECK, null, 0 );
                
                entranceTable.put( message, extendedState );
            }
            
            
            //undistinguishable selector:
            message = getEntranceMessage( Message.UNDISTINGUISHABLE_SELECTOR.getName() );
            
            state = executionModels[0].getEntranceState( message );
            
            extendedState =
                new ExtendedExecutionState( this, state, message, NO_SIGNATURE_CHECK, 
                        null, 0 );
            
            entranceTable.put( message, extendedState );
            
            
            fullModel = true;
        }
        
        public ExtendedExecutionModel( String selector ){
            Message message = getEntranceMessage( selector );
            
            ExecutionState state = executionModels[0].getEntranceState( message );
            
            ExtendedExecutionState extendedState =
                new ExtendedExecutionState( this, state, message, NO_SIGNATURE_CHECK, 
                        null, 0 );
            
            entranceTable.put( message, extendedState );
            
            
            fullModel = false;
        }
        
        public ExtendedExecutionModel( MethodInfo methodInfo, 
                int signatureCheck )
        {
            Message message = getEntranceMessage( methodInfo.name() );
            
            ExecutionState state = executionModels[0].getEntranceState( message );
            
            ExtendedExecutionState extendedState =
                new ExtendedExecutionState( this, state, message, signatureCheck, 
                        methodInfo, 0 );
            
            entranceTable.put( message, extendedState );
            
            
            fullModel = false;
        }
        
        public ExtendedExecutionModel( Target target, MethodInfo methodInfo,
                int signatureCheck )
        {
            //Message message = getEntranceMessage( )
            MessageSelector selector = new MessageSelector();
            selector.setName( methodInfo.name() );
            Message message = new Message( target, selector );
            
            ExecutionState state = executionModels[0].getEntranceState( message );
            
            ExtendedExecutionState extendedState =
                new ExtendedExecutionState( this, state, message, signatureCheck, 
                        methodInfo, 0 );
            
            entranceTable.put( message, extendedState );
            
            fullModel = false;
        }
        
        public Set getEntranceMessages() {
            return entranceTable.keySet();
        }

        public ExecutionState getEntranceState(Message message) {
            if ( !fullModel  ||  entranceTable.containsKey( message ) ){
                return (ExecutionState) entranceTable.get( message );
            }
            else{
                //create the entrance-state:
                ExecutionState state = executionModels[0].getEntranceState( message );
                ExtendedExecutionState newState = 
                    new ExtendedExecutionState( this, state, message, 
                            NO_SIGNATURE_CHECK, null, 0 );
                entranceTable.put( message, newState );
                return newState;
            }
        }

        public Enumeration getEntranceStates() {
            return entranceTable.elements();
        }

        public boolean isEntranceMessage(Message message) {
            return entranceTable.containsKey( message );
        }
    }
    
    
    private class ExtendedExecutionState extends ExecutionState{
        private ExtendedExecutionModel model;
        private ExecutionState baseState;
        private int signatureCheck;
        private MethodInfo signatureCheckInfo;
        private int layer;
        
        private Vector outTransitions;
        
        public ExtendedExecutionState( ExtendedExecutionModel model, 
                ExecutionState baseState, Message message, 
                int signatureCheck, MethodInfo signatureCheckInfo, int layer )
        {
            super( baseState.getFlowNode(), message, 
                    baseState.getSubstitutionSelector(), 
                    baseState.getSubstitutionTarget(), baseState.getStateType() );
            
            this.model = model;
            this.baseState = baseState;
            this.signatureCheck = signatureCheck;
            this.signatureCheckInfo = signatureCheckInfo;
            this.layer = layer;
        }
        
        public Enumeration getOutTransitions() {
            if ( outTransitions == null ){
                outTransitions = FireModel.this.getOutTransitions( this );
            }
            
            return outTransitions.elements();
        }
    }
    
    
    private class ExtendedExecutionTransition extends ExecutionTransition{
        private ExtendedExecutionModel model;
        private ExtendedExecutionState startState;
        private ExecutionTransition baseTransition;
        private ExtendedExecutionState endState;
        private boolean nextLayer;
        
        public ExtendedExecutionTransition( ExtendedExecutionModel model, 
                ExtendedExecutionState startState, 
                ExecutionTransition baseTransition, boolean nextLayer )
        {
            super( baseTransition.getLabel(), baseTransition.getFlowTransition() );
            
            this.model = model;
            this.startState = startState;
            this.baseTransition = baseTransition;
            this.nextLayer = nextLayer;
        }
        
        public ExtendedExecutionTransition( ExtendedExecutionState startState, ExtendedExecutionState endState ){
            super( "", null );
            
            this.startState = startState;
            this.endState = endState;
            this.nextLayer = true;
        }
        
        public ExecutionState getStartState(){
            return startState;
        }
        
        public ExecutionState getEndState() {
            if ( endState == null ){
                int newLayer;
                if ( nextLayer ){
                    newLayer = startState.layer + 1;
                }
                else{
                    newLayer = startState.layer;
                }
                endState = deriveState(
                        baseTransition.getEndState(), startState, newLayer );
                
                //if state already in 
                if ( model.stateCache.containsKey( endState ) ){
                    endState = (ExtendedExecutionState) model.stateCache.get( 
                            endState );
                }
                else{
                    model.stateCache.put( endState, endState );
                }
            }
            
            return endState;
        }
    }
    
    
}
