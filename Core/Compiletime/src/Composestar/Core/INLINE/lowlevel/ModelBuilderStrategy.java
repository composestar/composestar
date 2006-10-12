/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
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
import Composestar.Core.INLINE.model.While;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;

public class ModelBuilderStrategy implements LowLevelInlineStrategy{
    /**
     * The ModelBuilder
     */
    private ModelBuilder builder;

    /**
     * The complete instructionblock of the current inline
     */
    private Block inlineBlock;

    /**
     * The instruction block of the current scope
     */
    private Block currentBlock;


    /**
     * Contains all outer scope blocks of the current scope, the closest on top.
     */
    private Stack blockStack;

    /**
     * Indicates the current condition branch.
     */
    private Branch currentBranch;



    /**
     * Hashtable containing a mapping from integer labelid's to the corresponding Label object.
     */
    private Hashtable labelTable;
    
    
    /**
     * Hashtable containing a mapping from MethodInfo to integer id's
     */
    private Hashtable methodTable;
    private int lastMethodId;
    

    
    private int nextReturnActionId;
    private Switch onReturnInstructions;
    private ContextInstruction createActionStoreInstruction;
    private Label returnLabel;

    /**
     * Indicates whether the instructionset of the current inline is empty or not.
     * When it is empty, this indicates that the filters don't change the behaviour of the method(call)
     * and so no inline needs to be done on the given method(call)
     */
    private boolean empty;

    /**
     * The method(call) currently being inlined.
     */
    private MethodInfo currentMethod;

    /**
     * The current filterlabel.
     */
    private int currentLabelId;


    /**
     * Indicates that the next jump to end should be ignored (for error action).
     * FIXME replace this with conceptual change in FIRE
     */
    private boolean noJumpEnd;
    
    
    public ModelBuilderStrategy( ModelBuilder builder ){
        this.builder = builder;
        this.methodTable = new Hashtable(); 
        lastMethodId = 0;
    }


    /**
     * Returns the complete instructionblock of the current inline, or <code>null</code>when it is empty.
     * @return
     */
    public Block getInlineBlock(){
        if (empty){
            return null;
        }
        else{
            return inlineBlock;
        }
    }



    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startInline(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule[], Composestar.Core.LAMA.MethodInfo, java.lang.String[])
     */
    public void startInline(FilterModule[] filterSet, MethodInfo method, String[] argReferences){
        this.currentMethod = method;

        inlineBlock = new Block();
        blockStack = new Stack();
        labelTable = new Hashtable();
        currentLabelId = -1;
        
        nextReturnActionId = 0;
        onReturnInstructions = new Switch( new ContextExpression(ContextExpression.RETRIEVE_ACTION) );
        returnLabel = new Label( 9997 );

        empty = true;

        //create checkinnercall context instruction:
        Block block = new Block();
        
        ContextInstruction checkInnercall = new ContextInstruction( 
                ContextInstruction.CHECK_INNER_CALL, getMethodId( method ), block );
        inlineBlock.addInstruction( checkInnercall );
        
        //create CreateActionStore instruction:
        createActionStoreInstruction = 
            new ContextInstruction( ContextInstruction.CREATE_ACTION_STORE );
        block.addInstruction( createActionStoreInstruction );
        
        
        //set current block to inner block of checkInnercall instruction:
        currentBlock = block;
    }

    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endInline()
     */
    public void endInline(){
        //check whether there are on return actions:
        if ( !onReturnInstructions.hasCases() ){
            //remove createActionStore instruction:
            createActionStoreInstruction.setType( ContextInstruction.REMOVED );
            
            //set jumpLabel of onReturnJump to end:
            returnLabel.setId( 9998 );
        }
        else{
            //add onReturnActions:
            Block block = new Block();
            block.addInstruction( onReturnInstructions );
            
            While whileInstruction = 
                new While( new ContextExpression( ContextExpression.HAS_MORE_ACTIONS ),
                    block );
            whileInstruction.setLabel( returnLabel );
            
            
            currentBlock.addInstruction( whileInstruction );
        }
        
        ContextInstruction returnInstruction = new ContextInstruction(
                ContextInstruction.RETURN_ACTION );
        returnInstruction.setLabel( new Label( 9998) );
        currentBlock.addInstruction( returnInstruction );
        
        
        //create resetInnercall context instruction:
        ContextInstruction resetInnercall = new ContextInstruction( 
                ContextInstruction.RESET_INNER_CALL );
        resetInnercall.setLabel( getLabel( 9999 ) );
        inlineBlock.addInstruction( resetInnercall );
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startFilter(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter, int)
     */
    public void startFilter(Filter filter, int jumpLabel){
        Block filterBlock;

        currentLabelId = jumpLabel;

        filterBlock = new Block();
        filterBlock.setLabel( new Label( jumpLabel ) );
        currentBlock.addInstruction( filterBlock );
        
        pushBlock( filterBlock );
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endFilter()
     */
    public void endFilter(){
        popBlock();
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#evalCondExpr(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression)
     */
    public void evalCondExpr(ConditionExpression condition){
        Branch branch = new Branch( condition );
        this.currentBlock.addInstruction( branch );

        this.currentBranch = branch;
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#beginTrueBranch()
     */
    public void beginTrueBranch(){
        Block block = new Block();
        this.currentBranch.setTrueBlock( block );

        pushBlock( block );
    }

    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endTrueBranch()
     */
    public void endTrueBranch(){
        popBlock();
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#beginFalseBranch()
     */
    public void beginFalseBranch(){
        Block block = new Block();
        this.currentBranch.setFalseBlock( block );

        pushBlock( block );
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endFalseBranch()
     */
    public void endFalseBranch(){
        popBlock();
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#jump(int)
     */
    public void jump(int labelId){
        Label label;
        if ( labelId == -1 ){
            label = returnLabel;
            
            if ( noJumpEnd ){
            	noJumpEnd = false;
            	return;
            }
        }
        else if( labelId != currentLabelId + 1 ){
            label = getLabel( labelId );
        }
        else{
            return;
        }

        Jump jump = new Jump( label );

        this.currentBlock.addInstruction( jump );
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#generateAction(Composestar.Core.FIRE2.model.ExecutionState)
     */
    public void generateAction(ExecutionState state){
        Instruction instruction;

        FlowNode node = state.getFlowNode();
        if ( node.containsName( FlowChartNames.DISPATCH_ACTION_NODE ) ){
            generateDispatchAction( state );
        }
        else if ( node.containsName( FlowChartNames.META_ACTION_NODE ) ){//"before action" ) ){
//            generateBeforeAction( state );
            generateAfterAction( state );
        }
        else if ( node.containsName( "AfterAction" ) ){
            generateAfterAction( state );
        }
        else if ( node.containsName( "SkipAction" ) ){
            //jump to end:
            jump( -1 );
        }
        else if ( node.containsName( FlowChartNames.ERROR_ACTION_NODE ) ){
            instruction = new FilterAction( FlowChartNames.ERROR_ACTION_NODE, state.getMessage() );
            empty = false;
            currentBlock.addInstruction( instruction );
            noJumpEnd = true;
        }
        else if ( node.containsName( FlowChartNames.CONTINUE_ACTION_NODE ) ){
            instruction = new FilterAction( FlowChartNames.CONTINUE_ACTION_NODE, state.getMessage() );
            empty = false;
            currentBlock.addInstruction( instruction );
        }
        else if ( node.containsName( FlowChartNames.SUBSTITUTION_ACTION_NODE ) ){
            instruction = new FilterAction( FlowChartNames.SUBSTITUTION_ACTION_NODE, getCallMessage( state ) );
            empty = false;
            currentBlock.addInstruction( instruction );
        }
        else if ( node.containsName( FlowChartNames.CUSTOM_ACTION_NODE ) ){
            instruction = new FilterAction( "custom", getCallMessage( state ) );
            currentBlock.addInstruction( instruction );
        }
        else{
            throw new RuntimeException( "Unknown action" );
        }
    }
    
    private void generateDispatchAction( ExecutionState state ){
        Message callMessage = getCallMessage( state );

        ContextInstruction innerCallContext = setInnerCallContext( callMessage );
        if ( innerCallContext != null ){
            currentBlock.addInstruction( innerCallContext );
        }

        FilterAction action  = new FilterAction( FlowChartNames.DISPATCH_ACTION_NODE, callMessage );
        currentBlock.addInstruction( action );

        Target target = callMessage.getTarget();
        MessageSelector selector = callMessage.getSelector();
        if ( !Message.checkEquals( Message.INNER_TARGET, target )
                ||  !selector.getName().equals( 
                        currentMethod.name() ) )
        {
            empty = false;
        }
    }
    
    
    private void generateBeforeAction( ExecutionState state ){
        Message callMessage = getCallMessage( state );

        ContextInstruction innerCallContext = setInnerCallContext( callMessage );
        if ( innerCallContext != null ){
            currentBlock.addInstruction( innerCallContext );
        }

        FilterAction action = new FilterAction( "BeforeAction", callMessage );
        currentBlock.addInstruction( action );
        
        empty = false;
    }
    
    private void generateAfterAction( ExecutionState state ){
        Message callMessage = getCallMessage( state );
        
        int actionId = nextReturnActionId++;
        ContextInstruction storeInstruction = new ContextInstruction( ContextInstruction.STORE_ACTION,
                actionId );
        currentBlock.addInstruction( storeInstruction );

        Block block = new Block();
        
        
        
        ContextInstruction innerCallContext = setInnerCallContext( callMessage );
        if ( innerCallContext != null ){
            block.addInstruction( innerCallContext );
        }
        
        FilterAction action = new FilterAction( "AfterAction", callMessage );
        block.addInstruction( action );
        
        Case caseInstruction = new Case( actionId, block );
        onReturnInstructions.addCase( caseInstruction );
        
        empty = false;
    }
    

    /**
     * Checks whether the call is an innercall and whether the called method has inlined filters. Then
     * the innercall filtercontext needs to be set.
     * @param callMessage
     */
    private ContextInstruction setInnerCallContext( Message callMessage ){
        if ( Message.checkEquals( callMessage.getTarget(), Message.INNER_TARGET ) )
        {
            MethodInfo calledMethod;

            if ( callMessage.getSelector().getName().equals( currentMethod.name() ) ){
                calledMethod = currentMethod;
            }
            else{
                List parameterList = currentMethod.getParameters();
                String[] parameters = new String[parameterList.size()];
                for (int i=0; i<parameterList.size(); i++){
                    ParameterInfo parameter = (ParameterInfo) parameterList.get( i );
                    parameters[i] = parameter.parameterType().fullName();
                }

                calledMethod = currentMethod.parent().getMethod( 
                        callMessage.getSelector().getName(), parameters );
            }
            
            //it is possible that a called method could not be found, SIGN already has given a warning
            //or error for this
            if ( calledMethod == null ){
                return null;
            }

            ContextInstruction contextInstruction = 
                new ContextInstruction( ContextInstruction.SET_INNER_CALL, getMethodId( calledMethod ) );
            builder.addInnerCallCheckTask( contextInstruction );
            return contextInstruction;
        }
        else{
            return null;
        }
    }


    /**
     * Pushes an instruction block to the block stack (change to inner scope).
     * @param newBlock
     */
    private void pushBlock( Block newBlock ){
        this.blockStack.push( this.currentBlock );
        this.currentBlock = newBlock;
    }


    /**
     * Pops an instruction block from the block stack (change to outer scope).
     *
     */
    private void popBlock(){
        currentBlock = (Block) blockStack.pop();
    }

    /**
     * Creates the message that is called out of the original message and the
     * substitution selector and target
     * @param state
     * @return
     */
    private Message getCallMessage( ExecutionState state ){
        //get the dispatch target:
        Target dispTarget = state.getSubstitutionTarget();
        if ( Message.checkEquals( dispTarget, Message.STAR_TARGET ) )
            dispTarget = state.getMessage().getTarget();

        //get the dispatch selector:
        MessageSelector dispSelector = state.getSubstitutionSelector();
        if ( Message.checkEquals( dispSelector, Message.STAR_SELECTOR ) )
            dispSelector = state.getMessage().getSelector();

        return new Message( dispTarget, dispSelector );
    }


    /**
     * Returns the label corresponding with the given labelId. If the label doesn't
     * exist yet, it is created.
     * @param labelId
     * @return
     */
    private Label getLabel( int labelId ){
        Integer wrapper = new Integer( labelId );
        if ( labelTable.containsKey( wrapper ) ){
            return (Label) labelTable.get( wrapper );
        }
        else{
            Label label = new Label( labelId );
            labelTable.put( wrapper, label );
            return label;
        }
    }

    
    public int getMethodId( MethodInfo method ){
        if ( method == null ){
            int x = 9;
        }
        Integer id = (Integer) methodTable.get( method );
        if ( id == null ){
            id = new Integer( lastMethodId++ );
            methodTable.put( method, id );
        }
        
        return id.intValue();
    }
}
