/*
 * Created on 9-mrt-2006
 *
 */
package Composestar.Core.FIRE2.analysis;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.FIRE2.preprocessing.GrooveASTBuilder;
import Composestar.Core.FIRE2.preprocessing.Preprocessor;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class FireAnalysis {
    private Concern concern;
    private FlowModel[] flowModels;
    private ExecutionModel[] executionModels;
//    private Hashtable[] starSets, starExcludes;
//    private HashSet[] exitSet, exitExclude;
    
    public final static int NO_SIGNATURE_CHECK = 0;
    public final static int LOOSE_SIGNATURE_CHECK = 1;
    public final static int STRICT_SIGNATURE_CHECK = 2;
    
    private final static int SIGNATURE_MATCH_TRUE = 1;
    private final static int SIGNATURE_MATCH_FALSE = 2;
    private final static int SIGNATURE_MATCH_UNKNOWN = 3;
    
    private AnalysisState DEFAULT_STATE = new AnalysisState();
    
    
    public FireAnalysis( Concern concern, FilterModuleOrder order ){
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
    
    public FireAnalysis( Concern concern, FilterModule[] modules ){
        this.concern = concern;
        
        initialize( modules );
    }
    
//    private FilterModule[] addEndModule( FilterModule[] modules ){
//        if ( modules.length == 0 ){
//            FilterModule[] modules2 = { Preprocessor.END_MODULE };
//            return modules2;
//        }
//        
//        int last = modules.length-1;
//        
//        FirePreprocessingResult result = 
//            (FirePreprocessingResult) modules[last].getDynObject( 
//                    Preprocessor.RESULT_ID );
//        
//        ExecutionModel executionModel = result.getExecutionModel();
//        
//        //if the last executionmodule has an exit state, add endmodule:
//        if ( executionModel.getExitStates().hasMoreElements() ){
//            FilterModule[] modules2 = new FilterModule[ modules.length + 1 ];
//            for (int i=0; i<modules.length; i++){
//                modules2[i] = modules[i];
//            }
//            
//            modules2[modules2.length-1] = Preprocessor.END_MODULE;
//            return modules2;
//        }
//        else{
//            return modules;
//        }
//    }
    
    private void initialize( FilterModule[] modules ){
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
    
//    private void initialize( FilterModule[] modules ){
//        HashSet entranceSet, entranceExclude, removeTraces;
//        
//        this.flowModels = new FlowModel[ modules.length ];
//        this.executionModels = new ExecutionModel[ modules.length ];
//        
//        this.starSets = new Hashtable[ modules.length ];
//        this.starExcludes = new Hashtable[ modules.length ];
//        
//        this.exitSet = new HashSet[ modules.length ];
//        this.exitExclude = new HashSet[ modules.length ];
//        
//        
//        FirePreprocessingResult result;
//        for (int i=0; i<modules.length; i++){
//            result = (FirePreprocessingResult) modules[i].getDynObject( 
//                    Preprocessor.RESULT_ID );
//            
//            flowModels[i] = result.getFlowModel();
//            
//            try{
//                executionModels[i] = 
//                    (ExecutionModel) result.getExecutionModel().clone();
//            }
//            catch( CloneNotSupportedException exc ){
//                //should never occur.
//                throw new RuntimeException( "Unexpected CloneNotSupportedException", exc );
//            }
//        }
//        
//        //endmodule:
//        
//        
//        
//        entranceSet = new HashSet();
//        
//        entranceSet.add( new Message( Message.INNER_TARGET, Message.STAR_SELECTOR ) );
//        
//        entranceExclude = new HashSet();
//        entranceExclude.add( Message.STAR_MESSAGE );
//        
//        for (int i=0; i<executionModels.length; i++){
//            initializeExecutionModel( executionModels[i], entranceSet,
//                    entranceExclude, i );
//            
//            entranceSet = exitSet[i];
//            entranceExclude = exitExclude[i];
//        }
//    }
//    
//    private void initializeExecutionModel( ExecutionModel model,
//            HashSet entranceSet, HashSet entranceExclude, int index )
//    {   
//        HashSet removeTraces, starSet, starExclude;
//        Set entranceSelectors, exitSelectors;
//        Iterator iter;
//        Message message, comparer, targetGen, selectorGen;
//        
//        entranceSelectors = model.getEntranceSelectors();
//        
//        starSets[index] = new Hashtable();
//        starExcludes[index] = new Hashtable();
//        
//        //initialize star entrance:
//        //first partial star entrances:
//        iter = entranceSelectors.iterator();
//        while( iter.hasNext() )
//        {
//            message = (Message) iter.next();
//            if ( message.isGeneralization()  &&  
//                    !message.equals( Message.STAR_MESSAGE ) )
//            {
//                initializeStarEntrance( model, message, entranceSet,
//                        entranceExclude, entranceSelectors, index );
//            }
//        }
//        //then full star entrance:
//        if ( entranceSelectors.contains( Message.STAR_MESSAGE ) ){
//            initializeStarEntrance( model, Message.STAR_MESSAGE, entranceSet,
//                    entranceExclude, entranceSelectors, index );
//        }
//        
//        
//        //remove traces:
//        removeTraces = new HashSet();
//        iter = entranceSelectors.iterator();
//        while( iter.hasNext() ){
//            comparer = (Message) iter.next();
//            if ( comparer.isGeneralization() ){
//                starSet = (HashSet) starSets[index].get( comparer );
//                if ( starSet.isEmpty() ){
//                    removeTraces.add( comparer );
//                }
//            }
//            else{
//                //note first exclude than set:
//                if ( check( comparer, entranceExclude, entranceSet ) ){
//                    removeTraces.add( comparer );
//                }
//            }
//        }
//        
//        
//        iter = removeTraces.iterator();
//        while( iter.hasNext() ){
//            Message trace = (Message) iter.next();
//            model.removeTrace( trace );
//        }
//        
//        
//        
//        exitSet[index] = new HashSet();
//        exitSelectors = model.getExitSelectors();
//        iter = exitSelectors.iterator();
//        while( iter.hasNext() ){
//            comparer = (Message) iter.next();
//            if ( comparer.isGeneralization() ){
//                starSet = (HashSet) starSets[index].get( comparer );
//                starExclude = (HashSet) starExcludes[index].get( comparer );
//                
//                exitSet[index].addAll( starSet );
//                
//                //add comparer itself if it isn't contained yet in the 
//                //starSet or starExclude (is possible in the case that this comparer
//                //wasn't an entrancecomparer but is created by substitution):
//                if ( !starExclude.contains( comparer ) ){
//                    exitSet[index].add( comparer );
//                }
//            }
//            else{
//                exitSet[index].add( comparer );
//            }
//        }
//        
//        
//        exitExclude[index] = new HashSet();
//        iter = exitSelectors.iterator();
//        while( iter.hasNext() ){
//            comparer = (Message) iter.next();
//            if ( comparer.isGeneralization() ){
//                starSet = (HashSet) starSets[index].get( comparer );
//                starExclude = (HashSet) starExcludes[index].get( comparer );
//                
//                exitExclude[index].addAll( starExclude );
//                
//                //add comparer itself if it isn't contained yet in the 
//                //starSet or starExclude (is possible in the case that this comparer
//                //wasn't an entrancecomparer but is created by substitution):
//                if ( !starSet.contains( comparer ) ){
//                    exitExclude[index].add( comparer );
//                }
//            }
//        }
//        exitExclude[index].add( Message.STAR_MESSAGE );
//        exitExclude[index].removeAll( exitSet[index] );
//    }
//    
//    private void initializeStarEntrance( ExecutionModel model, 
//            Message entranceComparer, HashSet entranceSet, HashSet entranceExclude, 
//            Set entranceSelectors, int index )
//    {
//        Set exitSelectors;
//        Iterator iter;
//        Message message, comparer, m;
//        HashSet starSet, starExclude;
//        
//        starSet = new HashSet();
//        starSets[index].put( entranceComparer, starSet );
//        
//        starExclude = new HashSet();
//        starExcludes[index].put( entranceComparer, starExclude );
//        
//        if ( entranceComparer.equals( Message.STAR_MESSAGE ) ){
//            //starset:
//            iter = entranceSet.iterator();
//            while( iter.hasNext() ){
//                message = (Message) iter.next();
//                if ( !message.equals( Message.STAR_MESSAGE )  &&
//                        entranceSelectors.contains( message ) )
//                {
//                    continue;
//                }
//                
//                m = new Message( Message.STAR_TARGET, message.getSelector() );
//                if ( !m.equals( Message.STAR_MESSAGE )  &&  
//                        entranceSelectors.contains( m ) )
//                {
//                    continue;
//                }
//                
//                m = new Message( message.getTarget(), Message.STAR_SELECTOR );
//                if ( !m.equals( Message.STAR_MESSAGE )  &&  
//                        entranceSelectors.contains( m ) )
//                {
//                    continue;
//                }
//                
//                starSet.add( message );
//            }
//            
//            
//            //starexclude:
//            starExclude.addAll( entranceExclude );
//            iter = entranceSelectors.iterator();
//            while( iter.hasNext() ){
//                comparer = (Message) iter.next();
//                if ( !comparer.isGeneralization() ){
//                    starExclude.add( comparer );
//                }
//                else if ( !comparer.equals( Message.STAR_MESSAGE ) ){
//                    HashSet starSet2 = (HashSet) starSets[index].get( comparer );
//                    starExclude.addAll( starSet2 );
//                }
//            }
//        }
//        else if ( Message.checkEquals( entranceComparer.getTarget(),
//                Message.STAR_TARGET ) )
//        {
//            //starSet:
//            iter = entranceSet.iterator();
//            while( iter.hasNext() ){
//                message = (Message) iter.next();
//                
//                if ( !message.isGeneralization() ){
//                    if ( Message.checkEquals( message.getSelector(), 
//                            entranceComparer.getSelector() )  
//                            &&  !entranceSelectors.contains( message ) )
//                    {
//                        starSet.add( message );
//                    }
//                }
//                else if ( Message.checkEquals( message.getTarget(),
//                        Message.STAR_TARGET ) )
//                {
//                    m = new Message( message.getTarget(), 
//                            entranceComparer.getSelector() );
//                    if ( !entranceSelectors.contains( m )  &&  
//                            !entranceExclude.contains( m ) )
//                    {
//                        starSet.add( m );
//                    }
//                    
//                }
//            }
//            
//            
//            //starExclude:
//            //add messages with same selector as the entranceComparer:
//            iter = entranceExclude.iterator();
//            while( iter.hasNext() ){
//                message = (Message) iter.next();
//                if ( Message.checkEquals( message.getSelector(), 
//                        entranceComparer.getSelector() ) )
//                {
//                    starExclude.add( message );
//                }
//            }
//            
//            //add also the messages which have another specific entrancecomparer:
//            iter = entranceSelectors.iterator();
//            while( iter.hasNext() ){
//                comparer = (Message) iter.next();
//                if ( Message.checkEquals( comparer.getSelector(), 
//                        entranceComparer.getSelector() )
//                        &&  !comparer.equals( entranceComparer ) )
//                {
//                    starExclude.add( comparer );
//                }
//            }
//            
//            //add entrancecomparer to starset or starexclude:
//            if ( entranceSet.contains( entranceComparer ) ){
//                starSet.add( entranceComparer );
//            }
//            else if ( entranceExclude.contains( entranceComparer ) ){
//                starExclude.add( entranceComparer );
//            }
//            else if ( entranceSet.contains( Message.STAR_MESSAGE ) ){
//                starSet.add( entranceComparer );
//            }
//            else if ( entranceExclude.contains( Message.STAR_MESSAGE ) ){
//                starExclude.add( entranceComparer );
//            }
//        }
//        else if ( Message.checkEquals( entranceComparer.getSelector(), 
//                Message.STAR_SELECTOR ) )
//        {
//            //starSet:
//            iter = entranceSet.iterator();
//            while( iter.hasNext() ){
//                message = (Message) iter.next();
//                
//                if ( !message.isGeneralization() ){
//                    if ( Message.checkEquals( message.getTarget(), 
//                            entranceComparer.getTarget() )  
//                            &&  !entranceSelectors.contains( message ) )
//                    {
//                        starSet.add( message );
//                    }
//                }
//                else if ( !Message.checkEquals( message.getSelector(), 
//                        Message.STAR_SELECTOR ) )
//                {
//                    m = new Message( entranceComparer.getTarget(), 
//                            message.getSelector() );
//                    if ( !entranceSelectors.contains( m )  &&  
//                            !entranceExclude.contains( m ) )
//                    {
//                        starSet.add( m );
//                    }
//                    
//                }
//            }
//            
//            
//            //starExclude:
//            //add messages with same selector as the entranceComparer:
//            iter = entranceExclude.iterator();
//            while( iter.hasNext() ){
//                message = (Message) iter.next();
//                if ( Message.checkEquals( message.getTarget(), 
//                        entranceComparer.getTarget() ) )
//                {
//                    starExclude.add( message );
//                }
//            }
//            
//            //add also the messages which have another specific entrancecomparer:
//            iter = entranceSelectors.iterator();
//            while( iter.hasNext() ){
//                comparer = (Message) iter.next();
//                if ( Message.checkEquals( comparer.getTarget(),
//                        entranceComparer.getTarget() ) 
//                        &&  !comparer.equals( entranceComparer ) )
//                {
//                    starExclude.add( comparer );
//                }
//            }
//            
//            //add entrancecomparer to starset or starexclude:
//            if ( entranceSet.contains( entranceComparer ) ){
//                starSet.add( entranceComparer );
//            }
//            else if ( entranceExclude.contains( entranceComparer ) ){
//                starExclude.add( entranceComparer );
//            }
//            else if ( entranceSet.contains( Message.STAR_MESSAGE ) ){
//                starSet.add( entranceComparer );
//            }
//            else if ( entranceExclude.contains( Message.STAR_MESSAGE ) ){
//                starExclude.add( entranceComparer );
//            }
//        }
//        
//        
//        
//    }
    
    
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
    
    

    
    
    private Vector getOutTransitions( AnalysisState state )
    {
        if ( state.getStateType() == ExecutionState.EXIT_STATE )
            return getOutTransitionsCrossLayer( state );
        else
            return getOutTransitionsCurrentLayer( state );
    }
    
    private Vector getOutTransitionsCurrentLayer( AnalysisState state ){
        ExecutionTransition transition;
        int signatureCheck = state.signatureCheck;
        MethodInfo methodInfo = state.signatureCheckInfo;
        ExecutionState baseState = state.baseState;
        Enumeration baseEnum;
        
        if ( signatureCheck != NO_SIGNATURE_CHECK  &&  
                baseState.getFlowNode().containsName(
                        GrooveASTBuilder.SIGNATURE_MATCHING_ID) )
        {
            int result = signatureCheck( baseState, signatureCheck, methodInfo );
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
                Enumeration enum = baseState.getOutTransitions();
                while( enum.hasMoreElements() ){
                    transition = (ExecutionTransition) enum.nextElement();
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
                Enumeration enum = baseState.getOutTransitions();
                while( enum.hasMoreElements() ){
                    transition = (ExecutionTransition) enum.nextElement();
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
        
        //create analysistransitions:
        Vector outTransitions = new Vector();
        while( baseEnum.hasMoreElements() ){
            ExecutionTransition baseTransition = 
                (ExecutionTransition) baseEnum.nextElement();
            outTransitions.addElement( 
                    new AnalysisTransition( state, baseTransition, false ) );
        }
        
        return outTransitions;
    }
    
    private Vector getOutTransitionsCrossLayer( AnalysisState state )
    {
        Message m;
        ExecutionState returnState;
        int layer = state.layer;
        ExecutionState baseState = state.baseState;
        
        if ( layer == executionModels.length-1 ){
            return new Vector();
        }
        
        AnalysisState[] nextStates = new AnalysisState[1];
        
        nextStates[0] = 
            deriveState( executionModels[layer+1].getEntranceState( 
                    state.getMessage() ), state, layer+1 );
        
        if ( nextStates[0] == null ){
            //should not occur
            throw new RuntimeException( "No next state found, while" +
            " there should have been!" );
        }
        
        Vector result = new Vector();
        result.addElement( new AnalysisTransition( state, nextStates[0] ) );
        return result;
        
//        if ( !baseState.getMessage().isGeneralization() ){
//            AnalysisState[] nextStates = new AnalysisState[1];
//            
//            nextStates[0] = 
//                deriveState( executionModels[layer+1].getEntranceState( 
//                        baseState.getMessage() ), state, layer+1 );
//            
//            if ( nextStates[0] == null ){
//                //should not occur
//                throw new RuntimeException( "No next state found, while" +
//                " there should have been!" );
//            }
//            
//            Vector result = new Vector();
//            result.addElement( new AnalysisTransition( state, nextStates[0] ) );
//            return result;
//        }
//        else{
//            HashSet nextStates = new HashSet();
//            HashSet starSet = 
//                (HashSet) starSets[layer].get( baseState.getMessage() );
//            HashSet starExclude = 
//                (HashSet) starExcludes[layer].get( baseState.getMessage() );
//            Enumeration entranceStates = 
//                executionModels[layer+1].getEntranceStates();
//            
//            while( entranceStates.hasMoreElements() ){
//                returnState = (ExecutionState) entranceStates.nextElement();
//                m = returnState.getMessage();
//                
//                if ( m.isGeneralization() ){
//                    HashSet set = (HashSet) starSets[layer+1].get( m );
//                    Iterator iter = set.iterator();
//                    while( iter.hasNext() ){
//                        Message m2 = (Message) iter.next();
//                        if ( check( m2, starSet, starExclude ) ){
//                            nextStates.add( new AnalysisState( returnState, 
//                                    state.signatureCheck, state.signatureCheckInfo,
//                                    state.layer + 1) );
//                            break;
//                        }
//                    }
//                }
//                else{ 
//                    if ( check( m, starSet, starExclude ) ){
//                        nextStates.add( new AnalysisState( returnState, 
//                                state.signatureCheck, state.signatureCheckInfo,
//                                state.layer + 1) );
//                    }
//                }
//            }
//            
//            Vector result = new Vector();
//            Iterator iter = nextStates.iterator();
//            while( iter.hasNext() ){
//                AnalysisState nextState = (AnalysisState) iter.next();
//                AnalysisTransition transition = 
//                    new AnalysisTransition( state, nextState );
//                result.addElement( transition );
//            }
//            
//            return result;
//        }
    }
    
    private int signatureCheck( ExecutionState state, int signatureCheck, 
            MethodInfo methodInfo )
    {
        //check for signaturematching:
        if ( signatureCheck != NO_SIGNATURE_CHECK  &&  
                state.getFlowNode().containsName(
                        GrooveASTBuilder.SIGNATURE_MATCHING_ID) )
        {
            boolean flowTrue = false;
            boolean flowFalse = false;
            
            MatchingPart matchingPart = 
                (MatchingPart) state.getFlowNode().getRepositoryLink();
            
            //get the matching target:
            Target matchTarget = matchingPart.target;
            if ( Message.checkEquals( matchTarget, Message.STAR_TARGET ) )
                matchTarget = state.getMessage().getTarget();
            
            //get the matching selector:
            MessageSelector matchSelector = matchingPart.selector;
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
     * @param newState
     * @param startState
     * @return
     */
    private AnalysisState deriveState( ExecutionState newState, 
            ExecutionState startState, int layer )
    {
        AnalysisState startAnalysisState = (AnalysisState) startState;
        
        if ( !newState.getMessage().isGeneralization() ){
            return new AnalysisState( newState, startAnalysisState.signatureCheck, 
                    startAnalysisState.signatureCheckInfo, layer );
        }
        
        Message message = startState.getMessage();
        Message newStateMessage = newState.getMessage();
        Target derivedTarget = 
            ( Message.checkEquals( newStateMessage.getTarget(), 
                    Message.STAR_TARGET ) ?
                    message.getTarget() : newStateMessage.getTarget() );
        
        MessageSelector derivedSelector = 
            ( Message.checkEquals( newStateMessage.getSelector(), 
                    Message.STAR_SELECTOR ) ?
                            message.getSelector() : newStateMessage.getSelector() );
        
        Message derivedMessage = new Message( derivedTarget, derivedSelector );
        
        return new AnalysisState( newState.clone( derivedMessage ),
                startAnalysisState.signatureCheck, 
                startAnalysisState.signatureCheckInfo, layer );
    }
    
    
    private boolean check( Message message, HashSet set, HashSet exclude ){
        if ( set.contains( message ) ){
            return true;
        }
        else if ( exclude.contains( message ) ){
            return false;
        }
        else{
            Message selectorGen = 
                new Message( message.getTarget(), Message.STAR_SELECTOR );
            Message targetGen = 
                new Message( Message.STAR_TARGET, message.getSelector() );
            if ( set.contains( selectorGen )  ||  
                    set.contains( targetGen ) )
            {
                return true;
            }
            else if ( exclude.contains( selectorGen)  ||
                    exclude.contains( targetGen ) )
            {
                return false;
            }
            else if ( set.contains( Message.STAR_MESSAGE ) ){
                return true;
            }
            else{
                return false;
            }
        }
    }
    
    
    /**
     * Returns the startstate for a given selector.
     * @param selector
     * @return
     */
    public ExecutionState getStartState( String selector ){
        ExecutionState state = deriveStartState( selector );
        
        return new AnalysisState( state, NO_SIGNATURE_CHECK, null, 0 );
    }
    
    /**
     * Returns the startstate for a given methodInfo.
     * @param methodInfo The methodinfo
     * @param signatureCheck Indication whether a signatureCheck needs to be done.
     * @return
     */
    public ExecutionState getStartState( MethodInfo methodInfo, int signatureCheck ){
        ExecutionState state = deriveStartState( methodInfo.name() );
        
        return new AnalysisState( state, signatureCheck, methodInfo, 0 );
    }
    
    private ExecutionState deriveStartState( String selector ){
        Target t = new Target();
        t.name = "inner";
        MessageSelector s = new MessageSelector();
        s.name = selector;
        Message message = new Message( t, s );
        
        
        ExecutionState state = executionModels[0].getEntranceState( message );
        
        if ( state.getMessage().isGeneralization() )
            state = state.clone( message );
        
        return state;
    }
    
    public ExecutionState[] getStartStates(){
        HashSet distinguishable = getDistinguishable();
        Iterator iter = distinguishable.iterator();
        Vector startStates = new Vector();
        while( iter.hasNext() ){
            String selector = (String) iter.next();
            startStates.addElement( getStartState( selector ) );
        }
        
        startStates.addElement( 
                getStartState(Message.UNDISTINGUISHABLE_SELECTOR.getName()) );
        
//        HashSet startStates = new HashSet();
//        Enumeration enum = executionModels[0].getEntranceStates();
//        while( enum.hasMoreElements() ){
//            ExecutionState state = (ExecutionState) enum.nextElement();
//            
//            if ( state.getTarget().equals( Message.STAR_TARGET ) ){
//                Target target = new Target();
//                target.name = "inner";
//                Message message = new Message( target, state.getSelector() );
//                state = state.clone( message );
//            }
//            
//            startStates.add( new AnalysisState( state, NO_SIGNATURE_CHECK, null, 0 ) );
//        }
        
        return (ExecutionState[])  startStates.toArray( new ExecutionState[startStates.size()] );
    }
    
    public HashSet getDistinguishable(){
        HashSet distinguishable = new HashSet();
        for (int i=0; i<executionModels.length; i++){
            Set selectors = executionModels[i].getEntranceSelectors();
            Iterator iter = selectors.iterator();
            while( iter.hasNext() ){
                Message message = (Message) iter.next();
                if ( !Message.checkEquals( 
                        message.getSelector(), Message.STAR_SELECTOR) )
                {
                    distinguishable.add( message.getSelector().name );
                }
            }
        }
        
        return distinguishable;
    }
    
    
    private class AnalysisState extends ExecutionState{
        private ExecutionState baseState;
        private int signatureCheck;
        private MethodInfo signatureCheckInfo;
        private int layer;
        
        private Vector outTransitions;
        
        
        
        private AnalysisState(){
            super( null, null, null, null, null, 0 );
        }
        
        public AnalysisState( ExecutionState state, int signatureCheck, 
                MethodInfo signatureCheckInfo, int layer )
        {
            super( state.getFlowNode(), state.getSelector(), state.getTarget(), 
                    state.getSubstitutionSelector(), state.getSubstitutionTarget(),
                    state.getStateType() );
            
            this.baseState = state;
            this.signatureCheck = signatureCheck;
            this.signatureCheckInfo = signatureCheckInfo;
            this.layer = layer;
        }
        
        
        
        public void addInTransition(ExecutionTransition transition) {
        }
        
        public void addOutTransition(ExecutionTransition transition) {
        }
        
        public Enumeration getInTransitions() {
            throw new RuntimeException( "Not supported" );
        }
        public Enumeration getOutTransitions() {
            if ( outTransitions == null ){
                outTransitions = FireAnalysis.this.getOutTransitions( this );
                
                
            }
            
            return outTransitions.elements();
        }
        
        public boolean hasOutTransitions(){
            return getOutTransitions().hasMoreElements();
        }
        
        public void removeInTransition(ExecutionTransition transition) {
            throw new RuntimeException( "Not supported" );
        }
        public void removeOutTransition(ExecutionTransition transition) {
            throw new RuntimeException( "Not supported" );
        }
        
        
    }
    
    
    private class AnalysisTransition extends ExecutionTransition{
        private AnalysisState startState;
        private ExecutionTransition baseTransition;
        private AnalysisState endState;
        private boolean nextLayer;
        
        public AnalysisTransition( AnalysisState startState, 
                ExecutionTransition baseTransition, boolean nextLayer )
        {
            super( startState, baseTransition.getLabel(), DEFAULT_STATE, 
                    baseTransition.getFlowTransition() );
            
            this.startState = startState;
            this.baseTransition = baseTransition;
            this.nextLayer = nextLayer;
        }
        
        public AnalysisTransition( AnalysisState startState, AnalysisState endState ){
            super( startState, "", endState, null );
            
            this.startState = startState;
            this.endState = endState;
            this.nextLayer = true;
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
                endState = (AnalysisState) deriveState(
                        baseTransition.getEndState(), startState, newLayer );
            }
            
            return endState;
        }
    }
}
