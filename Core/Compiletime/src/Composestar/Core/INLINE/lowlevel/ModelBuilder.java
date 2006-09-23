/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

public class ModelBuilder implements CTCommonModule{
    private LowLevelInliner inliner;
    private ModelBuilderStrategy builderStrategy;

//  private Concern currentConcern;
    private HashSet inlinedMethodSet;
    private FilterModule[] modules;

    private FireModel currentFireModel;

    private DataStore dataStore;

    private Vector innerCallCheckTasks = new Vector();

    private static Hashtable inputFilterCode;
    private static Hashtable outputFilterCode;

    public ModelBuilder(){
        this.builderStrategy = new ModelBuilderStrategy( this );
        this.inliner = new LowLevelInliner( builderStrategy );
    }




    /**
     * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
     */
    public void run(CommonResources resources) throws ModuleException{
        dataStore = DataStore.instance();
        startInliner();

    }

    
    public static Block getInputFilterCode( MethodInfo method ){
        return (Block) inputFilterCode.get( method );
    }

    public static Block getOutputFilterCode( CallToOtherMethod call ){
        return (Block) outputFilterCode.get( call );
    }
    
    private void startInliner(){
        initialize();
        processInfo();
        storeInfo();
    }

    private void initialize(){
        inputFilterCode = new Hashtable();
        outputFilterCode = new Hashtable();
    }

    private void processInfo(){
        Iterator concerns = dataStore.getAllInstancesOf(Concern.class);

        while( concerns.hasNext() ){
            Concern concern = (Concern) concerns.next();
            processConcern( concern );
        }
    }


    private void processConcern( Concern concern ){
        //get inputFiltermodules:
        if ( concern.getDynObject("superImpInfo") == null ){
            return;
        }

        //initialize:
        innerCallCheckTasks = new Vector();
        inlinedMethodSet = new HashSet();

        //get filtermodules:
        FilterModuleOrder filterModules = 
            (FilterModuleOrder) concern.getDynObject( "SingleOrder" );

        List order = filterModules.orderAsList();
        modules = new FilterModule[ order.size() ];
        for (int i=0; i<order.size(); i++){
            String ref = (String) order.get( i );

            modules[i] = 
                (FilterModule) DataStore.instance().getObjectByID(ref);
        }


//      //add methods to methodtable, for faster retrieval:
//      Type type = (Type) concern.getPlatformRepresentation();
//      methodTable = new Hashtable();
//      List list = type.getMethods();
//      Iterator iter = list.iterator();
//      while( iter.hasNext() ){
//      MethodInfo method = (MethodInfo) iter.next();

//      Vector methods;
//      if ( methodTable.containsKey( method.name() ) ){
//      methods = (Vector) methodTable.get( method.name() );
//      }
//      else{
//      methods = new Vector();
//      methodTable.put( method.name(), methods );
//      }

//      methods.add( method );
//      }

        //create externals and internals:
        //TODO
//      structure.externals = getExternals();
//      structure.internals = getInternals();

        //iterate methods:
        Type type = (Type) concern.getPlatformRepresentation();
        List list = type.getMethods();
        Iterator iter = list.iterator();
        while( iter.hasNext() ){
            MethodInfo method = (MethodInfo) iter.next();

            processMethod( method );
        }


        //do innercall checks:
        for (int i=0; i<innerCallCheckTasks.size(); i++){
            ContextInstruction instruction = 
                (ContextInstruction) innerCallCheckTasks.elementAt(i);
            if (!inlinedMethodSet.contains( instruction.getMethod() ) ){
                instruction.setType( ContextInstruction.REMOVED );
            }
        }
    }


    private void processMethod( MethodInfo methodInfo ){
        //create executionmodel:
        ExecutionModel execModel = 
            currentFireModel.getExecutionModel( methodInfo, FireModel.STRICT_SIGNATURE_CHECK );

        //create inlineModel:
        inliner.inline( execModel, modules, methodInfo );

        //store inlineModel in methodElement:
        Block inlineBlock = builderStrategy.getInlineBlock();
        if ( inlineBlock != null ){
            inputFilterCode.put( methodInfo, inlineBlock );
            inlinedMethodSet.add( methodInfo );
        }


        //process calls:
        HashSet calls = methodInfo.getCallsToOtherMethods();
        Iterator iter = calls.iterator();
        while( iter.hasNext() ){
            CallToOtherMethod call = (CallToOtherMethod) iter.next();
            processCall( call );
        }

    }


    private void processCall( CallToOtherMethod call ){
        Block callBlock = new Block();
        Target target;
        Branch currentBranch = null;

        //retrieve target methodinfo:
        MethodInfo methodInfo = call.getCalledMethod();


        //create executionmodel for distinguishable targets and the undistinguishable target

        //Distinguishable:
        //TODO first do only undistinguishable. The implementation for doing also distinguishable
        //needs to be worked out further.
//      HashSet distTargets = currentFireModel.getDistinguishableTargets();
//      Iterator iter = distTargets.iterator();
//      while( iter.hasNext() ){
//      target = (Target) iter.next();

//      //get callblock:
//      Block block = createCallBlock( target, methodInfo );

//      //create branch to check target:
//      Branch branch = new Branch( new TargetSelectExpression( target.getName() ) );
//      branch.setTrueBlock( block );

//      block = new Block();
//      block.addInstruction( branch );

//      if ( currentBranch == null ){
//      //add first branch:
//      currentBranch = branch;
//      callBlock.addInstruction( currentBranch );
//      }
//      else{
//      //add new branch to previous branch:
//      currentBranch.setFalseBlock( block );
//      currentBranch = branch;
//      }
//      }

        //Undistinguishable:
        Block block = createCallBlock( Message.UNDISTINGUISHABLE_TARGET, methodInfo );
        if ( currentBranch == null ){
            //no distinguishable targets.
            //set callblock to the undistinguishable block:
            callBlock = block;
        }
        else{
            currentBranch.setFalseBlock( block );
        }

        //add callBlock to call
        outputFilterCode.put(call, callBlock );
    }


    private Block createCallBlock( Target target, MethodInfo methodInfo ){
        //create executionModel:
        ExecutionModel execModel = 
            currentFireModel.getExecutionModel( 
                    target, methodInfo, FireModel.STRICT_SIGNATURE_CHECK );

        //create inlineModel:
        inliner.inline( execModel, modules, methodInfo );

        //store inlineModel in callStructure:
        Block callBlock = builderStrategy.getInlineBlock();

        return callBlock;
    }


    private void storeInfo(){
        //TODO
    }



//  private MethodInfo getMethodInfo( CallToOtherMethod call ){
//  call.
//  //TODO
//  return null;
//  }

//  private MethodInfo getMethodInfo( String name, String[] parameters ){
//  //TODO get name out of fully qualified name:

//  if ( !methodTable.containsKey( name ) ){
//  return null;
//  }

//  Vector methods = (Vector) methodTable.get( name );

//  Enumeration enumer = methods.elements();
//  while( enumer.hasMoreElements() ){
//  MethodInfo methodInfo = (MethodInfo) enumer.nextElement();
//  if ( methodInfo.hasParameters( parameters ) ){
//  return methodInfo;
//  }
//  }

//  return null;
//  }



//  private FieldStructure[] getExternals(){
//  Vector externals = new Vector();
//  for (int i=0; i<modules.length; i++){
//  Iterator iter = modules[i].getExternalIterator();
//  while( iter.hasNext() ){
//  External external = (External) iter.next();
//  Target target = currentFireModel.getTarget( external );
//  FieldStructure field = new FieldStructure();
//  field.name = target.getName();
//  field.type = external.getType().getName();
//  field.reference = external.getValueExpression(0).getName();
//  }
//  }

//  return (FieldStructure[]) externals.toArray( new FieldStructure[externals.size()] );
//  }

//  private FieldStructure[] getInternals(){
//  Vector internals = new Vector();
//  for (int i=0; i<modules.length; i++){
//  Iterator iter = modules[i].getInternalIterator();
//  while( iter.hasNext() ){
//  Internal internal = (Internal) iter.next();
//  Target target = currentFireModel.getTarget( internal );
//  FieldStructure field = new FieldStructure();
//  field.name = target.getName();
//  field.type = internal.getType().getName();
//  }
//  }

//  return (FieldStructure[]) internals.toArray( new FieldStructure[internals.size()] );
//  }


//  private Enumeration getExternalTargets(){
//  Vector targets = new Vector();
//  for (int i=0; i<modules.length; i++){
//  Iterator iter = modules[i].getExternalIterator();
//  while( iter.hasNext() ){
//  External external = (External) iter.next();
//  targets.addElement( currentFireModel.getTarget( external ) );
//  }
//  }

//  return targets.elements();
//  }

//  private Enumeration getInternalTargets(){
//  Vector targets = new Vector();
//  for (int i=0; i<modules.length; i++){
//  Iterator iter = modules[i].getInternalIterator();
//  while( iter.hasNext() ){
//  Internal internal = (Internal) iter.next();
//  targets.addElement( currentFireModel.getTarget( internal ) );
//  }
//  }

//  return targets.elements();
//  }


    protected void addInnerCallCheckTask( ContextInstruction innerCallAction )
    {
        innerCallCheckTasks.add( innerCallAction );
    }



}
