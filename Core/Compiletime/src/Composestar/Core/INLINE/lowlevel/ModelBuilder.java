/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;

public class ModelBuilder{
    private LowLevelInliner inliner;
    private ModelBuilderStrategy builderStrategy;
    
    private Concern currentConcern;
    private Hashtable methodTable;
    private FilterModule[] modules;
    
    private FireModel currentFireModel;
    
    private DataStore dataStore = DataStore.instance();
    
    private Vector innerCallCheckTasks;
    
    
    public ModelBuilder(){
	this.builderStrategy = new ModelBuilderStrategy( this );
	this.inliner = new LowLevelInliner( builderStrategy );
    }
    
    
    private void startInliner(){
	ClassStructure[] classes = loadStructure();
	processInfo( classes );
	storeInfo( classes );
    }
    
    
    private ClassStructure[] loadStructure(){
	//TODO
	return null;
    }
    
    private void processInfo( ClassStructure[] classes ){
	for (int i=0; i<classes.length; i++){
	    processClass( classes[i] );
	}
    }
    
    
    private void processClass( ClassStructure structure ){
	//retrieve concern:
	currentConcern = (Concern) dataStore.getObjectByID( structure.fullyQualifiedName );
	
	//get inputFiltermodules:
	if ( currentConcern.getDynObject("superImpInfo") == null ){
	    return;
	}
	
	//initialize:
	innerCallCheckTasks = new Vector();
	
	//get filtermodules:
	FilterModuleOrder filterModules = 
	    (FilterModuleOrder) currentConcern.getDynObject( "SingleOrder" );

	List order = filterModules.orderAsList();
	modules = new FilterModule[ order.size() ];
	for (int i=0; i<order.size(); i++){
	    String ref = (String) order.get( i );

	    modules[i] = 
		(FilterModule) DataStore.instance().getObjectByID(ref);
	}
        
	
	//add methods to methodtable, for faster retrieval:
	Type type = (Type) currentConcern.getPlatformRepresentation();
	methodTable = new Hashtable();
	List list = type.getMethods();
	Iterator iter = list.iterator();
	while( iter.hasNext() ){
	    MethodInfo method = (MethodInfo) iter.next();
	    
	    Vector methods;
	    if ( methodTable.containsKey( method.name() ) ){
		methods = (Vector) methodTable.get( method.name() );
	    }
	    else{
		methods = new Vector();
		methodTable.put( method.name(), methods );
	    }
	    
	    methods.add( method );
	}
	
	//create externals and internals:
	structure.externals = getExternals();
	structure.internals = getInternals();
	
	//iterate methods:
	Enumeration methods = structure.methods.elements();
	while( methods.hasMoreElements() ){
	    MethodStructure methodStructure = (MethodStructure) methods.nextElement();
	    processMethod( methodStructure );
	}
	
	//do innercall checks:
	//TODO
	for (int i=0; i<innerCallCheckTasks.size(); i++){
	    
	}
    }
    
    
    private void processMethod( MethodStructure methodStructure ){
	//retrieve methodinfo object:
	MethodInfo methodInfo = getMethodInfo( methodStructure );
	
	//create executionmodel:
	ExecutionModel execModel = 
	    currentFireModel.getExecutionModel( methodInfo, FireModel.STRICT_SIGNATURE_CHECK );
	
	//create inlineModel:
	inliner.inline( execModel, modules, methodInfo );
	
	//store inlineModel in methodStructure:
	methodStructure.inputFilterCode = builderStrategy.getInlineBlock();
	
	//process calls:
	Enumeration calls = methodStructure.calls.elements();
	while( calls.hasMoreElements() ){
	    CallStructure callStructure = (CallStructure) calls.nextElement();
	    processCall( callStructure );
	}
	
    }
    
    
    private void processCall( CallStructure callStructure ){
	CallBlock callBlock;
	Target target;
	
	//retrieve target methodinfo:
	MethodInfo methodInfo = getMethodInfo( callStructure );
	
	//create executionmodel for each external and internal + one for self and unknown:
	//externals:
	Enumeration externals = getExternalTargets();
	while( externals.hasMoreElements() ){
	    //create executionModel:
	    target = (Target) externals.nextElement();
	    callBlock = createCallBlock( target, methodInfo );
	    callStructure.callBlocks.add( callBlock );
	}
	
	//internals:
	Enumeration internals = getInternalTargets();
	while( internals.hasMoreElements() ){
	    //create executionModel:
	    target = (Target) internals.nextElement();
	    callBlock = createCallBlock( target, methodInfo );
	    callStructure.callBlocks.add( callBlock );
	}
	
	//self:
	callBlock = createCallBlock( Message.SELF_TARGET, methodInfo );
	callStructure.callBlocks.add( callBlock );
	
	//unknown:
	callBlock = createCallBlock( Message.UNDISTINGUISHABLE_TARGET, methodInfo );
	callStructure.callBlocks.add( callBlock );
    }
    
    
    private CallBlock createCallBlock( Target target, MethodInfo methodInfo ){
	//create executionModel:
	ExecutionModel execModel = 
	    currentFireModel.getExecutionModel( 
		    target, methodInfo, FireModel.STRICT_SIGNATURE_CHECK );

	//create inlineModel:
	inliner.inline( execModel, modules, methodInfo );

	//store inlineModel in callStructure:
	CallBlock callBlock = new CallBlock();
	callBlock.target = target.getName();
	callBlock.outputFilterCode = builderStrategy.getInlineBlock();

	return callBlock;
    }
    
    
    private void storeInfo( ClassStructure[] classes ){
	//TODO
    }
    
    
    private MethodInfo getMethodInfo( MethodStructure structure ){
	return getMethodInfo( structure.fullyQualifiedName, structure.parameters );
    }
    
    private MethodInfo getMethodInfo( CallStructure structure ){
	return getMethodInfo( structure.fullyQualifiedName, structure.parameters );
    }
    
    private MethodInfo getMethodInfo( String name, String[] parameters ){
	//TODO get name out of fully qualified name:
	
	if ( !methodTable.containsKey( name ) ){
	    return null;
	}
	
	Vector methods = (Vector) methodTable.get( name );
	
	Enumeration enumer = methods.elements();
	while( enumer.hasMoreElements() ){
	    MethodInfo methodInfo = (MethodInfo) enumer.nextElement();
	    if ( methodInfo.hasParameters( parameters ) ){
		return methodInfo;
	    }
	}
	
	return null;
    }
    
    
    
    private FieldStructure[] getExternals(){
	Vector externals = new Vector();
	for (int i=0; i<modules.length; i++){
	    Iterator iter = modules[i].getExternalIterator();
	    while( iter.hasNext() ){
		External external = (External) iter.next();
		Target target = currentFireModel.getTarget( external );
		FieldStructure field = new FieldStructure();
		field.name = target.getName();
		field.type = external.getType().getName();
		field.reference = external.getValueExpression(0).getName();
	    }
	}
	
	return (FieldStructure[]) externals.toArray( new FieldStructure[externals.size()] );
    }
    
    private FieldStructure[] getInternals(){
	Vector internals = new Vector();
	for (int i=0; i<modules.length; i++){
	    Iterator iter = modules[i].getInternalIterator();
	    while( iter.hasNext() ){
		Internal internal = (Internal) iter.next();
		Target target = currentFireModel.getTarget( internal );
		FieldStructure field = new FieldStructure();
		field.name = target.getName();
		field.type = internal.getType().getName();
	    }
	}
	
	return (FieldStructure[]) internals.toArray( new FieldStructure[internals.size()] );
    }
    
    
    private Enumeration getExternalTargets(){
	Vector targets = new Vector();
	for (int i=0; i<modules.length; i++){
	    Iterator iter = modules[i].getExternalIterator();
	    while( iter.hasNext() ){
		External external = (External) iter.next();
		targets.addElement( currentFireModel.getTarget( external ) );
	    }
	}
	
	return targets.elements();
    }
    
    private Enumeration getInternalTargets(){
	Vector targets = new Vector();
	for (int i=0; i<modules.length; i++){
	    Iterator iter = modules[i].getInternalIterator();
	    while( iter.hasNext() ){
		Internal internal = (Internal) iter.next();
		targets.addElement( currentFireModel.getTarget( internal ) );
	    }
	}
	
	return targets.elements();
    }
    
    
    protected void addInnerCallCheckTask( ContextInstruction innerCallAction )
    {
        innerCallCheckTasks.add( innerCallAction );
    }
    
    
    private class ClassStructure{
	public String fullyQualifiedName;
	public FieldStructure[] internals;
	public FieldStructure[] externals;
	public Vector methods;
	
	public ClassStructure(){
	    methods = new Vector();
	}
    }
    
    private class MethodStructure{
	public String fullyQualifiedName;
	public String[] parameters;
	public Block inputFilterCode;
	
	public Vector calls;
	
	public MethodStructure(){
	    calls = new Vector();
	}
    }
    
    private class CallStructure{
	public String fullyQualifiedName;
	public String[] parameters;
	
	public Vector callBlocks;
	
	public CallStructure(){
	    callBlocks = new Vector();
	}
    }
    
    private class CallBlock{
	public String target;
	public Block outputFilterCode;
    }
    
    
    private class FieldStructure{
	public String name;
	public String type;
	public String reference;
    }
}
