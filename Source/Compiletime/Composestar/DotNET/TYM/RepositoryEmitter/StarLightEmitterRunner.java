/*
 * Created on 21-sep-2006
 *
 */
package Composestar.DotNET.TYM.RepositoryEmitter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ExternalConcernReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.INLINE.lowlevel.ModelBuilder;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.Case;
import Composestar.Core.INLINE.model.ContextExpression;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Switch;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.INLINE.model.While;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Repository.RepositoryAccess;
import Composestar.Repository.LanguageModel.CallElement;
import Composestar.Repository.LanguageModel.MethodBody;
import Composestar.Repository.LanguageModel.MethodElement;
import Composestar.Repository.LanguageModel.Reference;
import Composestar.Repository.LanguageModel.TypeElement;
import Composestar.Utils.Debug;

public class StarLightEmitterRunner implements CTCommonModule 
{
    private Vector callsToOtherMethods = new Vector();
    private RepositoryAccess repository;
    
    private INCRETimer emitTimer = INCRE.instance().getReporter().openProcess( 
    		"EMITTER", "storing to database", INCRETimer.TYPE_NORMAL );
    private INCRETimer commitTimer = INCRE.instance().getReporter().openProcess( 
    		"EMITTER", "comitting to database", INCRETimer.TYPE_NORMAL );

    public void run(CommonResources resources) throws ModuleException 
    {
        repository = new RepositoryAccess();
        
        // Emit all types to persistent repository
        emitTypes();
    }
    
    private void emitTypes() throws ModuleException
    {
    	int count = 0;
        DataStore dataStore = DataStore.instance();
        
        Iterator concernIterator = dataStore.getAllInstancesOf( Concern.class );
        while( concernIterator.hasNext() ){
            Concern concern = (Concern) concernIterator.next();
            DotNETType type = (DotNETType) concern.getPlatformRepresentation();
            
            if ( type == null )
                continue;
            
            if ( concern.getDynObject("superImpInfo") != null ){
                //get filtermodules:
                FilterModuleOrder order = 
                    (FilterModuleOrder) concern.getDynObject( "SingleOrder" );

//                TypeElement storedType = repository.GetTypeElement( type.fullName() );
                TypeElement storedType = type.getTypeElement();

                Iterator filterModules = order.orderAsList().iterator();
                while( filterModules.hasNext() ){
                    String ref = (String) filterModules.next();
                    FilterModule filterModule = (FilterModule) dataStore.getObjectByID( ref );
                    
                    //internals:
                    Iterator internals = filterModule.getInternalIterator();
                    
                    while( internals.hasNext() ){
                        Internal internal = (Internal) internals.next();
                        Composestar.Repository.LanguageModel.Internal storedInternal =
                            new Composestar.Repository.LanguageModel.Internal();
                        
                        //name:
                        storedInternal.set_Name( internal.getName() );
                        
                        //namespace:
                        StringBuffer namespace = new StringBuffer();
                        Enumeration packages = internal.getType().getPackage().elements();
                        while( packages.hasMoreElements() ){
                            namespace.append( packages.nextElement() );
                            if ( packages.hasMoreElements() ){
                                namespace.append( "." );
                            }
                        }
                        storedInternal.set_NameSpace( namespace.toString() );
                        
                        //typename:
                        storedInternal.set_Type( internal.getType().getName() );
                        
                        storedInternal.set_ParentTypeId( storedType.get_Id() );
                        
                        //store internal:
                        repository.storeInternal( storedInternal );
                    }
                    
                    
                    //externals:
                    Iterator externals = filterModule.getExternalIterator();
                    
                    while( externals.hasNext() ){
                        External external = (External) externals.next();
                        Composestar.Repository.LanguageModel.External storedExternal =
                            new Composestar.Repository.LanguageModel.External();
                        
                        //name:
                        storedExternal.set_Name( external.getName() );
                        
                        //reference:
                        ExternalConcernReference reference = external.getShortinit();
                        Reference storedReference = createReference( type, reference.getPackage(), 
                        		reference.getName(), reference.getInitSelector() );
                        storedExternal.set_Reference( storedReference );
                        
                        //type:
                        StringBuffer packages = new StringBuffer();
                        Enumeration enumer = external.getType().getPackage().elements();
                        while( enumer.hasMoreElements() ){
                            packages.append( enumer.nextElement() );
                            packages.append( '.' );
                        }
                        storedExternal.set_Type( packages.toString() + external.getType().getName() );
                        
                        //parent id:
                        storedExternal.set_ParentTypeId( storedType.get_Id() );
                        
                        //store external:
                        repository.storeExternal( storedExternal );
                    }
                    
                    
                    //conditions:
                    Iterator conditions = filterModule.getConditionIterator();
                    while( conditions.hasNext() ){
                        Condition condition = (Condition) conditions.next();
                        
                        Composestar.Repository.LanguageModel.Condition storedCondition =
                            new Composestar.Repository.LanguageModel.Condition();
                        
                        //name:
                        storedCondition.set_Name( condition.getName() );
                        
                        //reference:
                        Reference reference = createReference( type, 
                        		condition.getShortref().getPackage(), 
                                condition.getShortref().getName(), 
                                (String) condition.getDynObject( "selector" ) );
                        
                        storedCondition.set_Reference( reference );
                        
                        storedCondition.set_ParentTypeId( storedType.get_Id() );
                        
                        //store condition:
                        repository.storeCondition( storedCondition );
                    }
                }

                //emit methods:
                emitMethods( type, storedType );

                //store type:
                repository.storeTypeElement( storedType );
                
                //commit on every onehundred stored typeelements:
                count++;
                if ( count == 100 ){
                	commitTimer.start();
                	repository.commit();
                	commitTimer.stop();
                	count = 0;
                }
            }
        }
        
        commitTimer.start();
    	repository.commit();
    	commitTimer.stop();
    }
    
    private Reference createReference( Type type, Vector pack, String target,
            String selector )
    {
        Reference storedRef = new Reference();
        
        //namespace:
        StringBuffer namespace = new StringBuffer();
        Enumeration packages = pack.elements();
        while( packages.hasMoreElements() ){
            namespace.append( packages.nextElement() );
            if ( packages.hasMoreElements() ){
                namespace.append( '.' );
            }
        }
        storedRef.set_NameSpace( namespace.toString() );
        
        //selector:
        storedRef.set_Selector( selector );
        
        //target:
        storedRef.set_Target( target );
        
        //innercall context:
        if ( target.equals( "inner" ) ){
        	MethodInfo methodInfo = type.getMethod( selector, new String[0] );
        	if ( methodInfo != null ){
        		storedRef.set_InnerCallContext( ModelBuilder.getInnerCallContext( methodInfo ) );
        	}
        	else{
        		storedRef.set_InnerCallContext( -1 );
        	}
        }
        else{
        	storedRef.set_InnerCallContext( -1 );
        }
        
        return storedRef;
    }
    
    
    private void emitMethods(Type type, TypeElement storedType) throws ModuleException
    {
    	Debug.out( Debug.MODE_DEBUG, "Emitter",  "Emit type: " + type.fullName());
    	
        Iterator methods = type.getMethods().iterator();
        
        while( methods.hasNext() ){
        	DotNETMethodInfo method = (DotNETMethodInfo) methods.next();
//            Debug.out( Debug.MODE_DEBUG, "Emitter",  "  Emit method: " + method.name());
            MethodElement storedMethod;
            
            //get the block containing the filterinstructions:
            Block filterInstructions = ModelBuilder.getInputFilterCode( method );
            
            if ( filterInstructions != null ){
            
                //get stored method:
                String key = createKey( method );
                
                storedMethod = method.getMethodElement();
                storedMethod.set_HasInputfilters( true );
                Debug.out( Debug.MODE_DEBUG, "Emitter",  "key:" +key);

                if (storedMethod == null) {
                	Debug.out( Debug.MODE_ERROR, "Emitter", "Method not found : " + method.name() + "in type " + type.fullName() );
                	return;
                }
                
                //add inputfilter code:
                if (storedMethod.get_HasMethodBody()) {
                	MethodBody body = storedMethod.get_MethodBody();
//                	body.set_InputFilter( translateInstruction( filterInstructions ) );
                	body.set_InputFilter(translateInstruction(filterInstructions));
                }
                           
                //store methodElement:
//                Debug.out( Debug.MODE_DEBUG, "Emitter", "Storing method" + storedMethod.toString() );
                emitTimer.start();
                repository.storeMethodElement( storedMethod );
                emitTimer.stop();
                
                // emit calls:
                emitCalls( method, storedMethod );
            }
        }
    }
    
    private Hashtable createMethodIndex( List storedMethods ){
        Hashtable index = new Hashtable();
        
        Iterator methodIter = storedMethods.iterator();
        
        while( methodIter.hasNext() ){
            MethodElement method = (MethodElement) methodIter.next();
            String key = createKey( method );
            index.put( key, method );
        }
        
        return index;
    }
    
    private String createKey( MethodInfo method ){
        StringBuffer buffer = new StringBuffer();
        
        buffer.append( method.getReturnTypeString() );
        buffer.append( ' ' );
        buffer.append( method.parent().fullName() );
        
        buffer.append( "::" );
        
        buffer.append( method.name() );
        
        buffer.append( '(' );
        Iterator parameters = method.getParameters().iterator();
        while( parameters.hasNext() ){
            ParameterInfo parameter = (ParameterInfo) parameters.next();
            buffer.append( parameter.ParameterTypeString );//FIXME use parametertype instead
            if ( parameters.hasNext() ){
                buffer.append( ',' );
            }
        }
        buffer.append( ')' );
        
        return buffer.toString();
    }
    
    private String createKey( MethodElement method ){
        return method.get_Signature();
    }
    
    private void emitCalls( MethodInfo method, MethodElement storedMethod ){
        MethodBody body = storedMethod.get_MethodBody();
                
        Iterator calls = method.getCallsToOtherMethods().iterator();
        
        while( calls.hasNext() ){
            DotNETCallToOtherMethod call = (DotNETCallToOtherMethod) calls.next();
            
            //get stored call:
            String key = createKey( call );
            CallElement storedCall = call.getCallElement();
            
            //add outputfilter code:
            Block code = ModelBuilder.getOutputFilterCode( call );
            if ( code != null ){
            	String instructionXml = translateInstruction( code );
            	storedCall.set_OutputFilter( instructionXml );

            	Debug.out( Debug.MODE_DEBUG, "Emitter", "Storing call" + storedCall.toString() );
            	
            	//write call back:
            	repository.storeCallElement( storedCall );
            	
            	//tag MethodElement:
            	storedMethod.set_HasOutputFilters( true );
            	repository.storeMethodElement( storedMethod );
            }
        }
    }
    
    private Hashtable createCallIndex( List storedCalls ){
        Hashtable index = new Hashtable();
        
        Iterator callIter = storedCalls.iterator();
        
        while( callIter.hasNext() ){
            CallElement call = (CallElement) callIter.next();
            String key = createKey( call );
            index.put( key, call );
        }
        
        return index;
    }
    
    private String createKey( CallToOtherMethod call ){
        return call.getOperationName();
    }
    
    private String createKey( CallElement call ){
        return call.get_MethodReference();
    }
    
    private String translateInstruction( Block block ){
    	InstructionTranslater translater = InstructionTranslater.getInstance();
    	translater.start();
    	block.accept( translater );
    	return translater.getXml();
    }
    
    
    
    private static class InstructionTranslater implements Visitor
	{
		private final static InstructionTranslater INSTANCE = new InstructionTranslater();

		private Hashtable fullNameMap = new Hashtable();

		private StringBuffer buffer = new StringBuffer();

		private InstructionTranslater()
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction filterAction;

			DataStore dataStore = DataStore.instance();
			Iterator iter = dataStore
					.getAllInstancesOf(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction.class);
			while (iter.hasNext())
			{
				filterAction = (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction) iter
						.next();
				fullNameMap.put(filterAction.getName(), filterAction.getFullName());
			}
		}

		public static InstructionTranslater getInstance()
		{
			return INSTANCE;
		}
		
		public void start(){
			buffer = new StringBuffer();
			buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		}
		
		public void stop(){
			
		}
		
		public String getXml(){
			return buffer.toString();
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
		 */
		public Object visitBlock(Block block)
		{
			buffer.append("<Block");
			setLabel(block);
			buffer.append(">");
			
			Enumeration instructions = block.getInstructions();
			while (instructions.hasMoreElements())
			{
				Instruction instruction = (Instruction) instructions.nextElement();

				//buffer.append("<BlockItem>");
				instruction.accept(this);
				//buffer.append("</BlockItem>");
			}

			buffer.append("</Block>");

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
		 */
		public Object visitBranch(Branch branch)
		{
			buffer.append("<Branch");
			setLabel(branch);
			buffer.append(">");

			buffer.append("<ConditionExpression>");
			translateConditionExpression(branch.getConditionExpression());
			buffer.append("</ConditionExpression>");

			buffer.append("<TrueBlock>");
			branch.getTrueBlock().accept(this);
			buffer.append("</TrueBlock>");

			buffer.append("<FalseBlock>");
			branch.getFalseBlock().accept(this);
			buffer.append("</FalseBlock>");

			buffer.append("</Branch>");

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitContextInstruction(Composestar.Core.INLINE.model.ContextInstruction)
		 */
		public Object visitContextInstruction(ContextInstruction contextInstruction)
		{
			buffer.append("<ContextInstruction");
			setLabel(contextInstruction);

			buffer.append(" type=\"");
			buffer.append(contextInstruction.getType());
			buffer.append("\"");

			buffer.append(" code=\"");
			buffer.append(contextInstruction.getCode());
			buffer.append("\"");

			if (contextInstruction.getInstruction() != null)
			{
				buffer.append(">");
				contextInstruction.getInstruction().accept(this);
				//buffer.append("");
				buffer.append("</ContextInstruction>");
			}
			else
			{
				buffer.append("/>");
			}
				

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
		 */
		public Object visitFilterAction(FilterAction filterAction)
		{
			buffer.append("<FilterAction");
			setLabel(filterAction);

			buffer.append(" type=\"");
			buffer.append(filterAction.getType());
			buffer.append("\">");

			buffer.append("<OriginalMessage");
			createMessage(filterAction.getMessage());
			buffer.append("/>");

			buffer.append("<SubstitutedMessage");
			createMessage(filterAction.getSubstitutedMessage());
			buffer.append("/>");

			buffer.append("</FilterAction>");

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
		 */
		public Object visitJump(Jump jump)
		{
			buffer.append("<Jump");
			setLabel(jump);

			buffer.append(" target=\"");
			buffer.append(jump.getTarget().getId());
			buffer.append("\"/>");

			//buffer.append("</Jump>");

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitCase(Composestar.Core.INLINE.model.Case)
		 */
		public Object visitCase(Case caseInstruction)
		{
			buffer.append("<Case");
			setLabel(caseInstruction);

			buffer.append(" checkConstant=\"");
			buffer.append(caseInstruction.getCheckConstant());
			buffer.append("\">");

			buffer.append("<Instructions>");
			caseInstruction.getInstructions().accept(this);
			buffer.append("</Instructions>");

			buffer.append("</Case>");

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitSwitch(Composestar.Core.INLINE.model.Switch)
		 */
		public Object visitSwitch(Switch switchInstruction)
		{
			buffer.append("<Switch>");

			buffer.append("<Expression>");
			setExpression(switchInstruction.getExpression());
			buffer.append("</Expression>");

			Case[] cases = switchInstruction.getCases();
			for (int i = 0; i < cases.length; i++)
			{
				buffer.append("<CaseItems>");
				cases[i].accept(this);
				buffer.append("</CaseItems>");
			}

			buffer.append("</Switch>");

			return null;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitWhile(Composestar.Core.INLINE.model.While)
		 */
		public Object visitWhile(While whileInstruction)
		{
			buffer.append("<While>");

			buffer.append("<Expression>");
			setExpression(whileInstruction.getExpression());
			buffer.append("</Expression>");

			buffer.append("<Instructions>");
			whileInstruction.getInstructions().accept(this);
			buffer.append("</Instructions>");

			buffer.append("</While>");

			return null;
		}

		private void translateConditionExpression(ConditionExpression expression)
		{
			if (expression instanceof And)
			{
				And and = (And) expression;

				buffer.append("<And>");

				buffer.append("<Left>");
				translateConditionExpression(and.getLeft());
				buffer.append("</Left>");

				buffer.append("<Right>");
				translateConditionExpression(and.getRight());
				buffer.append("</Right>");

				buffer.append("</And>");
			}
			else if (expression instanceof Or)
			{
				Or or = (Or) expression;

				buffer.append("<Or>");

				buffer.append("<Left>");
				translateConditionExpression(or.getLeft());
				buffer.append("</Left>");

				buffer.append("<Right>");
				translateConditionExpression(or.getRight());
				buffer.append("</Right>");

				buffer.append("</Or>");
			}
			else if (expression instanceof Not)
			{
				Not not = (Not) expression;

				buffer.append("<Not>");

				buffer.append("<Operand>");
				translateConditionExpression(not.getOperand());
				buffer.append("</Operand>");

				buffer.append("</Not>");
			}
			else if (expression instanceof ConditionLiteral)
			{
				ConditionLiteral literal = (ConditionLiteral) expression;

				buffer.append("<Literal>");

				buffer.append("<Operand>");
				buffer.append(literal.getCondition().getRef().getName());
				buffer.append("</Operand>");

				buffer.append("</Literal>");
			}
			else if (expression instanceof True)
			{
				buffer.append("<True />");
			}
			else if (expression instanceof False)
			{
				buffer.append("<False />");
			}
		}

		private void createMessage(Message message)
		{
			//buffer.append("<Message");

			buffer.append(" target=\"");
			buffer.append(message.getTarget().getName());
			buffer.append("\"");

			buffer.append(" selector=\"");
			buffer.append(message.getSelector());
			buffer.append("\"");

			//buffer.append("</Message>");
		}

		private void setExpression(ContextExpression expression)
		{
			buffer.append("<ContextExpression");

			buffer.append(" type=\"");
			buffer.append(expression.getType());
			buffer.append("\"/>");

			//buffer.append("</ContextExpression>");
		}

		private void setLabel(Instruction instruction)
		{
			Label label = instruction.getLabel();

			if (label == null)
			{
				return;
			}

			buffer.append(" label=\"");
			buffer.append(label.getId());
			buffer.append("\"");
		}
	}

}