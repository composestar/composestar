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
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INLINE.lowlevel.ModelBuilder;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Repository.RepositoryAccess;
import Composestar.Repository.LanguageModel.CallElement;
import Composestar.Repository.LanguageModel.MethodBody;
import Composestar.Repository.LanguageModel.MethodElement;
import Composestar.Repository.LanguageModel.TypeElement;
import Composestar.Repository.LanguageModel.Inlining.InlineInstruction;

public class StarLightEmitterRunner implements CTCommonModule 
{
    private Vector callsToOtherMethods = new Vector();
    private RepositoryAccess repository;

    public void run(CommonResources resources) throws ModuleException 
    {
        repository = new RepositoryAccess();
        
        // Collect all types from the persistent repository
        emitTypes();
    }
    
    private void emitTypes() throws ModuleException
    {
        DataStore dataStore = DataStore.instance();
        
        Iterator concernIterator = dataStore.getAllInstancesOf( Concern.class );
        while( concernIterator.hasNext() ){
            Concern concern = (Concern) concernIterator.next();
            Type type = (Type) concern.getPlatformRepresentation();
            
            if ( type == null )
                continue;
            
            TypeElement storedType = repository.GetTypeElement( type.fullName() );
            
            //TODO store conditions, externals and internals
            
            emitMethods( type, storedType );
            
            repository.storeTypeElement( storedType );
        }
    }
    
    
    private void emitMethods(Type type, TypeElement storedType) throws ModuleException
    {
        //Iterator methods = type.getMethods().iterator();
        List storedMethods = repository.getMethodElements( storedType );
        Hashtable index = createMethodIndex( storedMethods );
        
        Iterator methods = type.getMethods().iterator();
        
        while( methods.hasNext() ){
            MethodInfo method = (MethodInfo) methods.next();
            
            //get stored method:
            String key = createKey( method );
            MethodElement storedMethod = (MethodElement) index.get( key );
            
            //add inputfilter code:
            MethodBody body = storedMethod.get_MethodBody();
            InlineInstruction instruction = 
                translateInstruction( ModelBuilder.getInputFilterCode( method ) );
            body.set_InputFilter( instruction );
            
            //emit calls:
            emitCalls( method, storedMethod );
            
            //store methodElement:
            repository.storeMethodElement( storedMethod );
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
        
        buffer.append( method.returnType().fullName() );
        buffer.append( ' ' );
        
        buffer.append( method.name() );
        
        buffer.append( '(' );
        Iterator parameters = method.getParameters().iterator();
        while( parameters.hasNext() ){
            ParameterInfo parameter = (ParameterInfo) parameters.next();
            buffer.append( parameter.parameterType().fullName() );
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
        
        List storedCalls = repository.getCallElements( storedMethod.get_MethodBody() );
        Hashtable index = createCallIndex( storedCalls );
        
        Iterator calls = method.getCallsToOtherMethods().iterator();
        
        while( calls.hasNext() ){
            CallToOtherMethod call = (CallToOtherMethod) calls.next();
            
            //get stored call:
            String key = createKey( call );
            CallElement storedCall = (CallElement) index.get( key );
            
            //add outputfilter code:
            InlineInstruction instruction =
                translateInstruction( ModelBuilder.getOutputFilterCode( call ) );
            storedCall.set_OutputFilter( instruction );
            
            //write call back:
            repository.storeCallElement( storedCall );
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
    
    private InlineInstruction translateInstruction( Block block ){
        return (InlineInstruction) block.accept( InstructionTranslater.getInstance() );
    }
    
    
    
    
    private static class InstructionTranslater implements Visitor{
        private Hashtable methodTable;
        private long lastId;
        private final static InstructionTranslater INSTANCE = new InstructionTranslater();
        
        
        private InstructionTranslater(){
            methodTable = new Hashtable();
            lastId = 0;
        }
        
        public static InstructionTranslater getInstance(){
            return INSTANCE;
        }
        
        private long getMethodId( MethodInfo method ){
            Long id = (Long) methodTable.get( method );
            if ( id == null ){
                id = new Long( lastId++ );
                methodTable.put( method, id );
            }
            
            return id.longValue();
        }
        
        
        /**
         * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
         */
        public Object visitBlock(Block block){
            Composestar.Repository.LanguageModel.Inlining.Block newBlock = 
                new Composestar.Repository.LanguageModel.Inlining.Block();
            
            setLabel( block, newBlock );
            
            
            Vector v = new Vector();
            
            Enumeration instructions = block.getInstructions();
            while( instructions.hasMoreElements() ){
                Instruction instruction = (Instruction) instructions.nextElement();
                v.add( instruction.accept( this ) );
            }
            
            InlineInstruction[] newInstructions = 
                (InlineInstruction[]) v.toArray( new InlineInstruction[v.size()] );
            
            newBlock.set_Instructions( newInstructions );
            
            return newBlock;
        }

        /**
         * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
         */
        public Object visitBranch(Branch branch){
            Composestar.Repository.LanguageModel.ConditionExpressions.ConditionExpression newCondExpr =
                translateConditionExpression( branch.getConditionExpression() );
            
            Composestar.Repository.LanguageModel.Inlining.Branch newBranch =
                new Composestar.Repository.LanguageModel.Inlining.Branch( newCondExpr );
            
            setLabel( branch, newBranch );
            
            Composestar.Repository.LanguageModel.Inlining.Block trueBlock =
                (Composestar.Repository.LanguageModel.Inlining.Block) branch.getTrueBlock().accept( this );
            
            Composestar.Repository.LanguageModel.Inlining.Block falseBlock =
                (Composestar.Repository.LanguageModel.Inlining.Block) branch.getFalseBlock().accept( this );
            
            newBranch.set_TrueBlock( trueBlock );
            newBranch.set_FalseBlock( falseBlock );
            
            return newBranch;
        }

        /**
         * @see Composestar.Core.INLINE.model.Visitor#visitContextInstruction(Composestar.Core.INLINE.model.ContextInstruction)
         */
        public Object visitContextInstruction(ContextInstruction contextInstruction){
            Block block = contextInstruction.getInnerBlock();
            Composestar.Repository.LanguageModel.Inlining.Block newBlock;
            if ( block != null ){
                newBlock = (Composestar.Repository.LanguageModel.Inlining.Block) block.accept( this );
            }
            else{
                newBlock = null;
            }
            
            long methodId = getMethodId( contextInstruction.getMethod() );
            
            
            Composestar.Repository.LanguageModel.Inlining.ContextInstruction newContextInstruction = 
                new Composestar.Repository.LanguageModel.Inlining.ContextInstruction( 
                        contextInstruction.getType(), methodId, newBlock );
            
            setLabel( contextInstruction, newContextInstruction );
            
            return newContextInstruction;
        }

        /**
         * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
         */
        public Object visitFilterAction(FilterAction filterAction){
            String selector = filterAction.getMessage().getSelector().getName();
            String target = filterAction.getMessage().getTarget().getName();
            
            Composestar.Repository.LanguageModel.Inlining.FilterAction newFilterAction = 
                new Composestar.Repository.LanguageModel.Inlining.FilterAction( 
                        filterAction.getType(), selector, target );
            
            setLabel( filterAction, newFilterAction );
            
            return newFilterAction;
        }

        /**
         * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
         */
        public Object visitJump(Jump jump){
            Composestar.Repository.LanguageModel.Inlining.Label newLabel =
                new Composestar.Repository.LanguageModel.Inlining.Label( 
                        jump.getTarget().getId() );
            
            Composestar.Repository.LanguageModel.Inlining.Jump newJump = 
                new Composestar.Repository.LanguageModel.Inlining.Jump( newLabel );
            
            setLabel( jump, newJump );
            
            return newJump;
        }
        
        
        private Composestar.Repository.LanguageModel.ConditionExpressions.ConditionExpression 
        translateConditionExpression( ConditionExpression expression )
        {
            if ( expression instanceof And ){
                And and = (And) expression;
                
                Composestar.Repository.LanguageModel.ConditionExpressions.And newAnd = 
                    new Composestar.Repository.LanguageModel.ConditionExpressions.And();
                
                newAnd.set_Left( translateConditionExpression( and.getLeft() ) );
                newAnd.set_Right( translateConditionExpression( and.getRight() ) );
                
                return newAnd;
            }
            else if ( expression instanceof Or ){
                Or or = (Or) expression;

                Composestar.Repository.LanguageModel.ConditionExpressions.Or newOr = 
                    new Composestar.Repository.LanguageModel.ConditionExpressions.Or();

                newOr.set_Left( translateConditionExpression( or.getLeft() ) );
                newOr.set_Right( translateConditionExpression( or.getRight() ) );

                return newOr;
            }
            else if ( expression instanceof Not ){
                Not not = (Not) expression;

                Composestar.Repository.LanguageModel.ConditionExpressions.Not newNot = 
                    new Composestar.Repository.LanguageModel.ConditionExpressions.Not();

                newNot.set_Operand( translateConditionExpression( not.getOperand() ) );

                return newNot;
            }
            else if ( expression instanceof ConditionLiteral ){
                ConditionLiteral literal = (ConditionLiteral) expression;
                
                Composestar.Repository.LanguageModel.ConditionExpressions.ConditionLiteral newLiteral = 
                    new Composestar.Repository.LanguageModel.ConditionExpressions.ConditionLiteral();
                newLiteral.set_Name( literal.getCondition().getRef().getName() );

                return newLiteral;
            }
            else if ( expression instanceof True ){
                Composestar.Repository.LanguageModel.ConditionExpressions.True newTrue =
                    new Composestar.Repository.LanguageModel.ConditionExpressions.True();
                
                return newTrue;
            }
            else if ( expression instanceof False ){
                Composestar.Repository.LanguageModel.ConditionExpressions.False newFalse =
                    new Composestar.Repository.LanguageModel.ConditionExpressions.False();
                
                return newFalse;
            }
            else{
                return null;
            }
        }
        
        
        private void setLabel( Instruction oldInstruction, InlineInstruction newInstruction ){
            Composestar.Repository.LanguageModel.Inlining.Label newLabel =
                new Composestar.Repository.LanguageModel.Inlining.Label( 
                        oldInstruction.getLabel().getId() );
            
            newInstruction.set_Label( newLabel );
        }
    }
}