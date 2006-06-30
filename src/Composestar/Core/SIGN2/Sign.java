/*
 * Created on 12-apr-2006
 *
 */
package Composestar.Core.SIGN2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.analysis.DepthFirstIterator;
import Composestar.Core.FIRE2.analysis.FireAnalysis;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.FIRE2.preprocessing.GrooveASTBuilder;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class Sign implements CTCommonModule{
    private HashSet unsolvedConcerns;
    private HashSet solvedConcerns;
    private Hashtable analysisModels;
    
    private final static int IN_SIGNATURE = 1;
    private final static int POSSIBLE = 2;
    private final static int NOT_IN_SIGNATURE = 3;
    
    private final static String[] META_PARAMS = 
    {"Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage"};
    
    private final static String MODULE_NAME = "SIGN";
    
    public Sign(){
        
    }
    
    
    /**
     * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
     */
    public void run(CommonResources resources) throws ModuleException {
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "Start signature generation and checking" );
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase0" );
        phase0();
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase1" );
        try{
            phase1();
        }
        catch( NullPointerException exc ){
            exc.printStackTrace();
        }
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase2" );
        phase2();
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase3" );
        phase3();
        
        printConcernMethods( resources );
        
        resources.addResource("signaturesmodified", Boolean.valueOf(true));
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "signature generation and checking done" );
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    ////                                                                   ////
    ////                     PHASE0                                        ////
    ////                                                                   ////
    ///////////////////////////////////////////////////////////////////////////
    
    
    private void phase0(){
        unsolvedConcerns = new HashSet();
        solvedConcerns = new HashSet();
        analysisModels = new Hashtable();
        
        Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
        while( conIter.hasNext() )
        {
            Concern concern = (Concern) conIter.next();
            
            if (concern.getDynObject("superImpInfo") != null){
                FilterModuleOrder filterModules = 
                    (FilterModuleOrder) concern.getDynObject( "SingleOrder" );
                FireAnalysis model = new FireAnalysis( concern, filterModules );
                analysisModels.put( concern, model );
                
                unsolvedConcerns.add( concern );
            }
            else{
                Signature signature = getSignature(concern);
        		LinkedList methods = getMethodList(concern);

        		// Add all (usr src) methods to the signature with status unknown.
        		for (int i = 0; i < methods.size(); i++)
        			signature.add ((MethodInfo) methods.get(i), MethodWrapper.NORMAL);
        		
        		signature.setStatus( Signature.SOLVED );
        		
        		solvedConcerns.add( concern );
            }
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    ////                                                                   ////
    ////                     PHASE1                                        ////
    ////                                                                   ////
    ///////////////////////////////////////////////////////////////////////////
    
    
    private void phase1(){
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase1-Resolve" );
        phase1Resolve();
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase1-Check" );
        phase1Check();
    }
    
    private void phase1Resolve(){
        boolean changed = true;
        Iterator iter;
        Concern concern;
        FireAnalysis model;
        HashSet distinguishable;
        Iterator messages;
        String message;
        
        //build signatures:
        while( changed ){
            changed = false;
            
            iter = unsolvedConcerns.iterator();
            while( iter.hasNext() ){
                concern = (Concern) iter.next();
                Signature signature = getSignature( concern );
                model = (FireAnalysis) analysisModels.get( concern );
                distinguishable = model.getDistinguishable();
                messages = distinguishable.iterator();
                
                //first distinguishable:
                while( messages.hasNext() ){
                    message = (String) messages.next();
                    changed = checkMessage( 
                            concern, model, message, distinguishable )  ||  changed;
                }
                
                //then undistinguishable:
                //use Method.UNDISTINGUISHABLE_SELECTOR as selector, 
                //because "*" as a selector causes
                //problems because it is treated as a generalization and not
                //as an undistinguishable selector.
                changed = checkMessage( concern, model, 
                        Message.UNDISTINGUISHABLE_SELECTOR.name, distinguishable )  
                        ||  changed;
            }
        }
    }
    
    
    private boolean checkMessage( Concern concern, FireAnalysis model, 
            String messageSelector, HashSet distinguishable )
    {
        boolean changed = false;
        DepthFirstIterator stateIterator;
        ExecutionState state;
        FlowNode node;
        Signature signature;
        MethodInfo[] methods;
        boolean nameMatching = false;
        boolean earlierSignatureMatch = true;
        Target signatureMatchingTarget = null;
        boolean dispatch = false;
        Vector metaStates = new Vector();
        
        signature = getSignature( concern );
        
        stateIterator = new DepthFirstIterator( model, messageSelector );
        while( stateIterator.hasNext() ){
            state = (ExecutionState) stateIterator.next();
            node = state.getFlowNode();
            
            
            //check whether a signaturematch is incountered in another filter than
            // a meta or dispatch filter. Then the status of the methods need to be
            // unknown, else normal
            if ( node.containsName( "Filter" ) ){
                Filter filter = (Filter) node.getRepositoryLink();
                if ( filter.type.type != FilterType.META  &&  
                        filter.type.type != FilterType.DISPATCH  &&  
                        signatureMatchingTarget != null )
                {
                    earlierSignatureMatch = true;
                }
                signatureMatchingTarget = null;
            }
            
            
            if ( node.containsName( "NameMatchingPart" ) ){
                nameMatching = true;
            }
            else if ( node.containsName( "SignatureMatchingPart" ) ){
                nameMatching = false;
                MatchingPart matchingPart = (MatchingPart) node.getRepositoryLink();
                signatureMatchingTarget = matchingPart.target;
                if ( Message.checkEquals( signatureMatchingTarget, Message.STAR_TARGET ) ){
                    signatureMatchingTarget = state.getTarget();
                }
            }
            else if ( node.containsName( GrooveASTBuilder.META_ACTION_ID ) )
            {
                Object[] obj = new Object[3];
                obj[0] = new Boolean( nameMatching );
                obj[1] = signatureMatchingTarget;
                obj[2] = state;
                metaStates.add( obj );
            }
            else if( node.containsName( GrooveASTBuilder.DISPATCH_ACTION_ID ) )
            {   
                methods = getMethods( concern, messageSelector, state, 
                        nameMatching, signatureMatchingTarget, distinguishable );
                
                //check whether there are dispatch methods. When there are none
                //this might mean that flow might never reach this part, due to a meta
                //filter and so the default methods must be added for the meta-dispatch
                if ( methods.length > 0 )
                    dispatch = true;
                
                for (int j=0; j<methods.length; j++){
                    if ( !signature.hasMethod( methods[j] ) ){
                        signature.add( methods[j], 
                                earlierSignatureMatch ? MethodWrapper.UNKNOWN :
                                    MethodWrapper.NORMAL );
                        changed = true;
                    }
                }
            }
//            else if ( state.getStateType() == ExecutionState.EXIT_STATE 
//                    &&  !state.hasOutTransitions() )
//            {   
//                methods = getMethods( concern, messageSelector, state, 
//                        true, null, distinguishable );
//                
//                //check whether there are dispatch methods. When there are none
//                //this might mean that flow might never reach this part, due to a meta
//                //filter and so the default methods must be added for the meta-dispatch
//                if ( methods.length > 0 )
//                    dispatch = true;
//                
//                for (int j=0; j<methods.length; j++){
//                    if ( !signature.hasMethod( methods[j] ) ){
//                        signature.add( methods[j], 
//                                earlierSignatureMatch ? MethodWrapper.UNKNOWN :
//                                    MethodWrapper.NORMAL );
//                        changed = true;
//                    }
//                }
//            }
            
            
        }
        
        if ( !dispatch ){
            for (int i=0; i<metaStates.size(); i++){
                Object[] obj = (Object[]) metaStates.elementAt( i );
                
                methods = getMethods( concern, messageSelector, 
                        (ExecutionState) obj[2], ((Boolean) obj[0]).booleanValue(), 
                        (Target) obj[1], distinguishable );
                
                for (int j=0; j<methods.length; j++){
                    //remove parameters:
                    MethodInfo m = methods[j].getClone( methods[j].name(), 
                            methods[j].parent() );
                    m.Parameters = new ArrayList();
                    
                    if ( !signature.hasMethod( m ) ){
                        signature.add( m, MethodWrapper.UNKNOWN );
                        changed = true;
                    }
                }
            }
        }
        
        return changed;
    }
    
    private MethodInfo[] getMethods( Concern concern, String selector, 
            ExecutionState state, boolean nameMatching, 
            Target signatureMatchingTarget, HashSet distinguishable )
    {
        //case 2:
        if ( !selector.equals( Message.UNDISTINGUISHABLE_SELECTOR.name ) ){
            return createFromTarget( concern, state, selector );
        }
        //case 7:
        else if ( !Message.checkEquals( state.getSelector(), Message.UNDISTINGUISHABLE_SELECTOR ) ){
            Debug.out( Debug.MODE_ERROR, MODULE_NAME, "Dispatch structure " +
            		"in the filterset on concern '" + concern.getName() + 
            		"' leads to infinite signature!", 
            		state.getFlowNode().getRepositoryLink() );
            
            return new MethodInfo[0];
        }
        //case 3:
        else if ( nameMatching  &&  !Message.checkEquals( 
                state.getSubstitutionSelector(), Message.STAR_SELECTOR ) )
        {
            Debug.out( Debug.MODE_ERROR, MODULE_NAME, "Dispatch structure " +
            		"in the filterset on concern '" + concern.getName() + 
            		"' leads to infinite signature!", 
            		state.getFlowNode().getRepositoryLink() );
            
            return new MethodInfo[0];
        }
        //case 4:
        else if ( nameMatching  &&  Message.checkEquals( 
                state.getSubstitutionSelector(), Message.STAR_SELECTOR ) )
        {
            if ( state.getTarget().getName().equals( "inner" ) )
                return getInnerMethods( concern, state, distinguishable );
            else{
                return getTargetMethods( concern, state, distinguishable );
            }
                
        }
        //case 5 and 6
        else if ( !nameMatching )
        {
            if ( signatureMatchingTarget.getName().equals( "inner" ) )
                return getInnerMethods( concern, state, distinguishable );
            else
                return getTargetMethods( concern, state, signatureMatchingTarget,
                        distinguishable );
        }
        //case 1:
        else{
            return new MethodInfo[0];
        }
    }
    
    private MethodInfo[] createFromTarget( Concern concern, ExecutionState state, 
            String selector )
    {
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get dispatchtarget concern and methods:
        String dispatchMethodName = dispSelector.getName();
        List methods;
        if ( dispTarget.name.equals( "inner" ) ){
            methods = getMethodList( concern );
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            Concern targetConcern = ref.getRef().getType().getRef();
            
            Signature signature = getSignature( targetConcern );
            methods = signature.getMethods();
        }
        
        Vector result = new Vector();
        for (int i=0; i<methods.size(); i++){
            MethodInfo method = (MethodInfo) methods.get( i );
            if( method.Name.equals( dispatchMethodName ) ){
                MethodInfo newMethod = method.getClone( 
                        selector, (Type) concern.getPlatformRepresentation() );
                result.addElement( newMethod );
            }
        }
        
        return (MethodInfo[]) result.toArray( new MethodInfo[result.size()] );
    }
    
    private MethodInfo[] getInnerMethods( Concern concern, ExecutionState state,
            HashSet distinguishable )
    {
        Vector result = new Vector();
        
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get the targetconcern:
        Concern targetConcern;
        if ( dispTarget.name.equals( "inner" ) ){
            targetConcern = concern;
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            targetConcern = ref.getRef().getType().getRef();
        }
        Type targetType = (Type) targetConcern.getPlatformRepresentation();
        
        //get the inner methods:
        LinkedList methods = getMethodList(concern);

        
        //check for each method whether it is not distinguishable and
        //whether the corresponding dispatchselector is in the dispatchtarget:
		for (int i = 0; i < methods.size(); i++){
		    MethodInfo method = (MethodInfo) methods.get( i );
		    
		    //check not distinguishable:
		    if ( distinguishable.contains( method.Name ) )
		        continue;
		    
		    //check dispatchselector in dispatchtarget:
		    MethodInfo targetMethod;
		    if ( Message.checkEquals( dispSelector, Message.UNDISTINGUISHABLE_SELECTOR ) ){
		        targetMethod = method.getClone( method.Name, targetType );
		    }
		    else{
		        targetMethod = method.getClone( dispSelector.name, targetType );
		    }
		    
		    if ( dispTarget.name.equals( "inner" ) ){
		        //if inner, check inner methods:
		        
		        if ( methods.contains( targetMethod ) )
		            result.addElement( method );
		    }
		    else{
		        //get the signature of the dispatch target:
		        Signature targetSignature = getSignature( targetConcern );
		        
		        //else check signature methods:
		        if ( targetSignature.hasMethod( targetMethod ) )
		            result.addElement( method );
		    }
		}
		
		//return the result:
		return (MethodInfo[]) result.toArray( new MethodInfo[ result.size() ] );
    }
    
    private MethodInfo[] getTargetMethods( Concern concern, ExecutionState state,
            HashSet distinguishable )
    {
        Vector result = new Vector();
        Type type = (Type) concern.getPlatformRepresentation();
        
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get the methods in the targetsignature:
        DeclaredObjectReference ref = 
            (DeclaredObjectReference) dispTarget.getRef();
        Concern targetConcern = ref.getRef().getType().getRef();
        Signature targetSignature = getSignature( targetConcern );
        List methods = targetSignature.getMethods();
        
        
        //check for each method whether it is not distinguishable and
        //add it to the signature of the concern:
		for (int i = 0; i < methods.size(); i++){
		    MethodInfo method = (MethodInfo) methods.get( i );
		    
		    if ( distinguishable.contains( method.Name ) )
		        continue;
		    
		    MethodInfo newMethod = method.getClone( method.Name, type );
		    result.addElement( newMethod );
		}
		
		return (MethodInfo[]) result.toArray( new MethodInfo[ result.size() ] );
    }
    
    private MethodInfo[] getTargetMethods( Concern concern, ExecutionState state, 
            Target donor, HashSet distinguishable )
    {
        Vector result = new Vector();
        
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get target signature or innermethods:
        Concern targetConcern;
        if ( dispTarget.name.equals( "inner" ) ){
            targetConcern = concern;
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            targetConcern = ref.getRef().getType().getRef();
        }
        
        Type targetType = (Type) targetConcern.getPlatformRepresentation();
        Signature targetSignature = null;
        List innerMethods = null;
        //signature only relevant when dispatchTarget != inner
        if ( !dispTarget.name.equals( "inner" ) ){
            targetSignature = getSignature( targetConcern );
        }
        //innermethods only relevant when dispatchTarget == inner
        else{
            innerMethods = getMethodList( targetConcern );
        }

        //get donor methods:
        DeclaredObjectReference ref = (DeclaredObjectReference) donor.getRef();
        if ( ref == null ){
            int x =1;
        }
        Concern donorConcern = ref.getRef().getType().getRef();
        Signature donorSignature = getSignature( donorConcern );
        List methods = donorSignature.getMethods();
        
		for (int i = 0; i < methods.size(); i++){
		    MethodInfo method = (MethodInfo) methods.get( i );
		    
		    //check not distinguishable:
		    if ( distinguishable.contains( method.Name ) )
		        continue;
		    
		    //check dispatchselector in dispatchtarget:
		    
		    //first create targetMethod: 
		    MethodInfo targetMethod;
		    //this makes the distinction between case 6 and 5:
		    if ( Message.checkEquals( dispSelector, Message.UNDISTINGUISHABLE_SELECTOR ) ){
		        targetMethod = method.getClone( method.Name, targetType );
		    }
		    else{
		        targetMethod = method.getClone( dispSelector.name, targetType );
		    }
		    
		    //then do the check:
		    if ( dispTarget.name.equals( "inner" ) ){
		        //if inner, check inner methods:
		        if ( innerMethods.contains( targetMethod ) )
		            result.addElement( method );
		    }
		    else{
		        //else check signature methods:
		        if ( targetSignature.hasMethod( targetMethod ) )
		            result.addElement( method );
		    }
		}
		
		return (MethodInfo[]) result.toArray( new MethodInfo[ result.size() ] );
    }
    
    
    private void phase1Check(){
        Iterator iter;
        Concern concern;
        FireAnalysis model;
        HashSet distinguishable;
        Iterator selectors;
        String selector;
        MethodInfo method;
        
        //check for nondispatchable of distinguishable and inner selectors:
        iter = unsolvedConcerns.iterator();
        while( iter.hasNext() ){
            concern = (Concern) iter.next();
            model = (FireAnalysis) analysisModels.get( concern );
            distinguishable = model.getDistinguishable();
            selectors = distinguishable.iterator();
            
            //first distinguishable:
            while( selectors.hasNext() ){
                selector = (String) selectors.next();
                checkNonDispatchable( concern, model, selector );
            }
            
            //then inner undistinguishable:
            HashSet checkedSelectors = new HashSet();
            LinkedList methods = getMethodList(concern);
            for (int i=0; i<methods.size(); i++){
                method = (MethodInfo) methods.get( i );
                selector = method.name();
                if ( !distinguishable.contains( selector )  &&
                        !checkedSelectors.contains( selector ) )
                {
                    checkNonDispatchable( concern, model, selector );
                    checkedSelectors.add( selector );
                }
            }
        }
    }
    
    /**
     * Checks whether a given selector is not added to the signature because
     * of an not existing dispatch.
     * 
     * @param selector
     */
    private void checkNonDispatchable( Concern concern, FireAnalysis model,
            String selector )
    {
        DepthFirstIterator stateIterator;
        ExecutionState state;
        FlowNode node;
        Signature signature = getSignature( concern );
        
        //don't do the check when the signature has the given selector:
        if ( signature.hasMethod(selector) )
            return;
        
        stateIterator = new DepthFirstIterator( model, selector );
        while( stateIterator.hasNext() ){
            state = (ExecutionState) stateIterator.next();
            node = state.getFlowNode();
            
            if( node.containsName( GrooveASTBuilder.DISPATCH_ACTION_ID )  ||  
                    node.containsName( GrooveASTBuilder.META_ACTION_ID ) )
            {
                //get the dispatch target:
                Target dispTarget = state.getSubstitutionTarget();
                if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
                    dispTarget = state.getMessage().getTarget();
                
                //get the dispatch selector:
                MessageSelector dispSelector = state.getSubstitutionSelector();
                if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
                    dispSelector = state.getMessage().getSelector();
                
                //get the dispatch target:
                Concern targetConcern;
                if ( dispTarget.name.equals( "inner" ) ){
                    targetConcern = concern;
                }
                else{
                    DeclaredObjectReference ref = 
                        (DeclaredObjectReference) dispTarget.getRef();
                    targetConcern = ref.getRef().getType().getRef();
                }
                
                Debug.out( Debug.MODE_WARNING, MODULE_NAME, "Selector '" 
                        + selector + "' is not added" +
                        " to the signature of concern '" + concern.name + "' " +
                        "because the dispatch target '" + dispTarget.name + "(" +
                        targetConcern.name + ")' does not contain method '" +
                        dispSelector.name + "'", node.getRepositoryLink() );
            }
        }
    }
    
    
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    ////                                                                   ////
    ////                     PHASE2                                        ////
    ////                                                                   ////
    ///////////////////////////////////////////////////////////////////////////
    
    
    
    private void phase2(){
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase2-Resolve" );
        phase2Resolve();
        
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "phase2-Check" );
        phase2Check();
    }
    
    private void phase2Resolve(){
        boolean changed = true;
        Iterator iter, iter2;
        Concern concern;
        FireAnalysis model;
        MethodWrapper wrapper;
        
        //build signatures:
        while( changed ){
            changed = false;
            iter = unsolvedConcerns.iterator();
            
            while( iter.hasNext() ){
                concern = (Concern) iter.next();
                Signature signature = getSignature( concern );
                model = (FireAnalysis) analysisModels.get( concern );
                
                iter2 = signature.getMethodWrapperIterator();
                
                while( iter2.hasNext() ){
                    wrapper = (MethodWrapper) iter2.next();
                    if ( wrapper.RelationType == MethodWrapper.UNKNOWN ){
                        int result = resolveMethodDispatch( concern, model, 
                                wrapper.theMethodInfo );
                        
                        if ( result == IN_SIGNATURE ){
                            wrapper.RelationType = MethodWrapper.NORMAL;
                            changed = true;
                        }
                        else if ( result == NOT_IN_SIGNATURE ){
                            wrapper.RelationType = MethodWrapper.REMOVED;
//                            signature.removeMethodWrapper( wrapper );
                            changed = true;
                        }
                    }
                }
            }
        }
    }
    
    private int resolveMethodDispatch( Concern concern, FireAnalysis model, 
            MethodInfo methodInfo )
    {
        ExecutionState state;
        FlowNode node;
        DepthFirstIterator iterator;
        
        //first check with strict signature checks:
        iterator = new DepthFirstIterator( model, methodInfo, 
                FireAnalysis.STRICT_SIGNATURE_CHECK );
        
        while( iterator.hasNext() ){
            state = (ExecutionState) iterator.next();
            node = state.getFlowNode();
            
            if( node.containsName( GrooveASTBuilder.DISPATCH_ACTION_ID ) )
            {
                int result = resolveDispatchExistence( concern, methodInfo, state );
                if ( result == IN_SIGNATURE )
                    return IN_SIGNATURE;
            }
            else if ( node.containsName( GrooveASTBuilder.META_ACTION_ID ) )
            {
                int result = resolveMetaExistence( concern, methodInfo, state );
                if ( result == IN_SIGNATURE )
                    return IN_SIGNATURE;
            }
        }
        
        //then check again with loose signature checks:
        iterator = new DepthFirstIterator( model, methodInfo, 
                FireAnalysis.LOOSE_SIGNATURE_CHECK );
        
        while( iterator.hasNext() ){
            state = (ExecutionState) iterator.next();
            node = state.getFlowNode();
            
            if( node.containsName( GrooveASTBuilder.DISPATCH_ACTION_ID ) )
            {
                int result = resolveDispatchExistence( concern, methodInfo, state );
                if ( result != NOT_IN_SIGNATURE )
                    return POSSIBLE;
            }
            else if( node.containsName( GrooveASTBuilder.META_ACTION_ID ) )
            {
                int result = resolveMetaExistence( concern, methodInfo, state );
                if ( result != NOT_IN_SIGNATURE )
                    return POSSIBLE;
            }
        }
        
        //else return not in signature:
        return NOT_IN_SIGNATURE;
    }
    
    private int resolveDispatchExistence( Concern concern, MethodInfo method, 
            ExecutionState state )
    {
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get dispatchtarget concern and methods:
        String dispatchMethodName = dispSelector.getName();
        List methods;
        if ( dispTarget.name.equals( "inner" ) ){
            Type type = (Type) concern.getPlatformRepresentation();
            MethodInfo targetMethod = method.getClone( dispSelector.getName(), type );
            
            methods = getMethodList( concern );
            if ( methods.contains( targetMethod ) ){
                return IN_SIGNATURE;
            }
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            Concern targetConcern = ref.getRef().getType().getRef();
            
            Type type = (Type) concern.getPlatformRepresentation();
            MethodInfo targetMethod = method.getClone( dispSelector.getName(), type );
            
            Signature signature = getSignature( targetConcern );
            if ( signature.hasMethod( targetMethod ) ){
                MethodWrapper wrapper = signature.getMethodWrapper( targetMethod );
                if ( wrapper.getRelationType() == MethodWrapper.UNKNOWN ){
                    return POSSIBLE;
                }
                else if ( wrapper.getRelationType() == MethodWrapper.REMOVED ){
                    return NOT_IN_SIGNATURE;
                }
                else{
                    return IN_SIGNATURE;
                }
            }
        }
        
        return NOT_IN_SIGNATURE;
    }
    
    private int resolveMetaExistence( Concern concern, MethodInfo method, 
            ExecutionState state )
    {
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get dispatchtarget concern and methods:
        String dispatchMethodName = dispSelector.getName();
        List methods;
        if ( dispTarget.name.equals( "inner" ) ){
            Type type = (Type) concern.getPlatformRepresentation();
            
			MethodInfo m = type.getMethod(dispSelector.getName(),META_PARAMS);
            if ( m != null ){
                return IN_SIGNATURE;
            }
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            Concern targetConcern = ref.getRef().getType().getRef();
            
            Type type = (Type) concern.getPlatformRepresentation();
            MethodInfo targetMethod = method.getClone( dispSelector.getName(), type );
            
            
            Signature signature = getSignature( targetConcern );
            
            MethodWrapper wrapper = getMethodWrapper( signature, 
                    dispSelector.getName(), META_PARAMS );
            
            if ( wrapper != null ){
                if ( wrapper.getRelationType() == MethodWrapper.UNKNOWN ){
                    return POSSIBLE;
                }
                else if ( wrapper.getRelationType() == MethodWrapper.REMOVED ){
                    return NOT_IN_SIGNATURE;
                }
                else{
                    return IN_SIGNATURE;
                }
            }
        }
        
        return NOT_IN_SIGNATURE;
    }
    
    private void phase2Check(){
        Concern concern;
        FireAnalysis model;
        MethodWrapper wrapper;
        Iterator iter, iter2;
        
        iter = unsolvedConcerns.iterator();
        
        while( iter.hasNext() ){
            concern = (Concern) iter.next();
            Signature signature = getSignature( concern );
            model = (FireAnalysis) analysisModels.get( concern );
            
            iter2 = signature.getMethodWrapperIterator();
            
            while( iter2.hasNext() ){
                wrapper = (MethodWrapper) iter2.next();
                
                //check for cyclic dependancies:
                if ( wrapper.RelationType == MethodWrapper.UNKNOWN ){
                    Debug.out( Debug.MODE_ERROR, MODULE_NAME, "Cyclic signature " +
                    		"dependancy found on method '" + concern.getName() 
                    		+ "." + wrapper.theMethodInfo.Name + "'" );
                }
                
                //check for unexisting dispatches:
                MethodInfo info = wrapper.getMethodInfo();
                checkMethodDispatch( concern, model, info );
            }
        }
    }
    
    private void checkMethodDispatch( Concern concern, FireAnalysis model, 
            MethodInfo methodInfo )
    {
        ExecutionState state;
        FlowNode node;
        DepthFirstIterator iterator;
        
        iterator = new DepthFirstIterator( model, methodInfo, 
                FireAnalysis.STRICT_SIGNATURE_CHECK );
        
        while( iterator.hasNext() ){
            state = (ExecutionState) iterator.next();
            node = state.getFlowNode();
            
            if( node.containsName( GrooveASTBuilder.DISPATCH_ACTION_ID ) )
            {
                checkDispatchExistence( concern, methodInfo, state );
            }
            else if( node.containsName( GrooveASTBuilder.META_ACTION_ID ) )
            {
                checkMetaExistence( concern, methodInfo, state );
            }
        }
    }
    
    
    private void checkDispatchExistence( Concern concern, MethodInfo method, 
            ExecutionState state )
    {
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get dispatchtarget concern and methods:
        String dispatchMethodName = dispSelector.getName();
        List methods;
        if ( dispTarget.name.equals( "inner" ) ){
            Type type = (Type) concern.getPlatformRepresentation();
            MethodInfo targetMethod = method.getClone( dispSelector.getName(), type );
            
            methods = getMethodList( concern );
            if ( !methods.contains( targetMethod ) ){
                for (int i=0; i<methods.size(); i++){
                    MethodInfo m = (MethodInfo) methods.get( i );
                    if ( m.name().equals( targetMethod.name() ) ){
                        Debug.out( Debug.MODE_WARNING, MODULE_NAME,
                                "The methodcall to method " +
                                methodInfoString( method ) + " in concern " +
                                concern.name + " might be dispatched to method " +
                                m.name() + " in inner with the wrong parameters " +
                                "and/or return type!", 
                                state.getFlowNode().getRepositoryLink() );
                        return;
                    }
                }
                
                Debug.out( Debug.MODE_WARNING, MODULE_NAME, 
                        "The methodcall to method " +
                        methodInfoString( method ) + " in concern " +
                        concern.name + " might be dispatched to the unresolved " +
                        "method " + targetMethod.name() + " in inner",
                        state.getFlowNode().getRepositoryLink() );
            }
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            Concern targetConcern = ref.getRef().getType().getRef();
            
            Type type = (Type) concern.getPlatformRepresentation();
            MethodInfo targetMethod = method.getClone( dispSelector.getName(), type );
            
            Signature signature = getSignature( targetConcern );
            if ( !signature.hasMethod( targetMethod ) ){
                if ( signature.hasMethod( targetMethod.name() ) ){
                    Debug.out( Debug.MODE_WARNING, MODULE_NAME,
                            "The methodcall to method " +
                            methodInfoString( method ) + " in concern " +
                            concern.name + " might be dispatched to method " +
                            targetMethod.name() + " in concern " + 
                            targetConcern.getName() + 
                    		" with the wrong parameters and/or return type!",
                    		state.getFlowNode().getRepositoryLink() );
                }
                else{
                    Debug.out( Debug.MODE_WARNING, MODULE_NAME,
                            "The methodcall to method " +
                            methodInfoString( method ) + " in concern " +
                            concern.name + " might be dispatched to the unresolved " +
                            "method " + targetMethod.name() + 
                            " in concern " + targetConcern.getName(),
                            state.getFlowNode().getRepositoryLink() );
                }
            }
        }
    }
    
    
    private void checkMetaExistence( Concern concern, MethodInfo method, 
            ExecutionState state )
    {
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();
        
        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();
        
        //get dispatchtarget concern and methods:
        String dispatchMethodName = dispSelector.getName();
        List methods;
        if ( dispTarget.name.equals( "inner" ) ){
            Type type = (Type) concern.getPlatformRepresentation();
            
			MethodInfo m = type.getMethod(dispSelector.getName(),META_PARAMS);
            if ( m == null ){
                Debug.out( Debug.MODE_ERROR, MODULE_NAME,
                        "The methodcall to method '" +
                        methodInfoString( method ) + "' in concern '" +
                        concern.name + "' might lead to a meta-call to an" +
                        " unresolved meta-method '" +
                        m.name() + "' in inner!",
                        state.getFlowNode().getRepositoryLink() );
            }
        }
        else{
            DeclaredObjectReference ref = 
                (DeclaredObjectReference) dispTarget.getRef();
            Concern targetConcern = ref.getRef().getType().getRef();
            
            Type type = (Type) concern.getPlatformRepresentation();
            MethodInfo targetMethod = method.getClone( dispSelector.getName(), type );
            
            
            Signature signature = getSignature( targetConcern );
            
            MethodWrapper wrapper = getMethodWrapper( signature, 
                    dispSelector.getName(), META_PARAMS );
            
            if ( wrapper == null ){
                Debug.out( Debug.MODE_ERROR, MODULE_NAME,
                        "The methodcall to method '" +
                        methodInfoString( method ) + "' in concern '" +
                        concern.name + "' might lead to a meta-call to an" +
                        " unresolved meta-method '" +
                        dispSelector.getName() + "' in concern '" + 
                        targetConcern.getName() + "'!",
                        state.getFlowNode().getRepositoryLink() );
            }
        }
    }
    
    
    
    private MethodWrapper getMethodWrapper( Signature signature, String name,
            String[] types)
    {
        Iterator iter = signature.getMethodWrapperIterator();
        while( iter.hasNext() ){
            MethodWrapper wrapper = (MethodWrapper) iter.next();
            MethodInfo method = wrapper.getMethodInfo();
            
            // if same name && param length
            if( method.name().equals( name ) && method.hasParameters( types ) ) {
                return wrapper;
            }
        }
        return null;
    }
    
    private LinkedList getMethodList(Concern c)
    {       
        Type dt = (Type) c.getPlatformRepresentation();
        if (dt == null) return new LinkedList();
        
        return new LinkedList(dt.getMethods());
    }
    
    private Signature getSignature(Concern c)
    {
        Signature signature = c.getSignature();
        if (signature == null) 
        {
            signature = new Signature();
            c.setSignature(signature);
        }
        
        return signature;
    }
    
    
    
    
    private String methodInfoString( MethodInfo info ){
        StringBuffer buffer = new StringBuffer();
        buffer.append( info.name() );
        
        buffer.append( '(' );
        List parameters = info.getParameters();
        for (int i=0; i<parameters.size(); i++){
            if ( i > 0 )
                buffer.append( ", " );
            
            ParameterInfo parameter = (ParameterInfo) parameters.get( i );
            buffer.append( parameter.ParameterTypeString );
        }
        buffer.append( ')' );
        
        return buffer.toString();
    }
    
    
    public void phase3()
    {
        
        DataStore datastore = DataStore.instance();
        Iterator conIter = datastore.getAllInstancesOf(Concern.class);
        
        while( conIter.hasNext() )
        {
            Concern concern = (Concern)conIter.next();
            
            LinkedList dnmi = getMethodList(concern);
            Signature signature = concern.getSignature();
            
            for (int i = 0; i < dnmi.size(); i++)
            {
                MethodInfo methodInfo = (MethodInfo) dnmi.get(i);
                MethodWrapper wrapper = signature.getMethodWrapper(methodInfo);
                
                if ( wrapper == null ){
                    signature.add (methodInfo, MethodWrapper.REMOVED);
                }
                else if (wrapper.getRelationType() == MethodWrapper.ADDED)
                {
                    wrapper.setRelationType(MethodWrapper.NORMAL);										
                }
            }
            
            List normal = signature.getMethodWrappers(MethodWrapper.NORMAL);
            Iterator normalItr = normal.iterator();
            while(normalItr.hasNext())
            {
                MethodWrapper mw = (MethodWrapper)normalItr.next();
                MethodInfo minfo = mw.getMethodInfo();
                if(!dnmi.contains(minfo)){
                    mw.setRelationType( MethodWrapper.ADDED );
                }
            }
        }
    }
    
    public void printConcernMethods(CommonResources resources)
    {
        boolean signaturesmodified = false;
        DataStore datastore = DataStore.instance();
        
        // Get all the concerns
        Iterator conIter = DataStore.instance().getAllInstancesOf(Concern.class);
        
        while( conIter.hasNext() )
        {
            Concern concern = (Concern)conIter.next();
            
            Signature st = concern.getSignature();
            if (st != null && concern.getDynObject("superImpInfo") != null)
            {
                Debug.out (Debug.MODE_INFORMATION, "Sign", "\tSignature for concern: "+concern.getQualifiedName());
                
                // Show them your goodies.
                Iterator itr = (st.getMethodWrappers()).iterator();
                
                while (itr.hasNext())
                {
                    MethodWrapper mw = (MethodWrapper) itr.next();
                    if(mw.getRelationType()==MethodWrapper.REMOVED || mw.getRelationType()==MethodWrapper.ADDED)
                        signaturesmodified = true;
                    
                    String relation = "?";
                    if(mw.getRelationType()==MethodWrapper.ADDED) relation = "added";
                    if(mw.getRelationType()==MethodWrapper.REMOVED) relation = "removed";
                    if(mw.getRelationType()==MethodWrapper.NORMAL) relation = "kept";
                    
                    //TODO: remove this, needed for demo!
                    if(!Configuration.instance().getProperty("Platform").equalsIgnoreCase("c"))
                    {
                        String returntype = mw.theMethodInfo.returnType().Name ;
                        
                        String parameters = "";
                        Iterator itrpara = mw.theMethodInfo.getParameters().iterator();
                        while (itrpara.hasNext())
                        {
                            ParameterInfo parainfo = (ParameterInfo) itrpara.next();
                            if (parameters.equalsIgnoreCase("") && parainfo.parameterType() != null) {
                                parameters = parainfo.parameterType().Name;
                            }
                            else {
                                parameters = parameters + ", " + parainfo.Name;
                            }
                            
                        }
                        Debug.out (Debug.MODE_INFORMATION, "Sign", "\t\t[ " + relation +  " ]  (" + returntype + ") " + mw.getMethodInfo().name() + "(" + parameters + ")");
                    }
                }
            }
        }
        
        resources.addResource("signaturesmodified", Boolean.valueOf(signaturesmodified));
    }
}
