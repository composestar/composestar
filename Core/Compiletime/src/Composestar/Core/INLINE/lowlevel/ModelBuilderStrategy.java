/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowChartNames;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.LAMA.MethodInfo;

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
     * Vector containing the instructionblocks of the after filters, to be 
     * placed after the blocks of the other filters.
     */
    private Vector afterFilterBlocks;

    /**
     * Indicates the last afterfilter, useful for the jump in a following skip action.
     */
    private Block lastAfterFilter;

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


    public ModelBuilderStrategy( ModelBuilder builder ){
        this.builder = builder;
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
            return currentBlock;
        }
    }



    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startInline(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule[], Composestar.Core.LAMA.MethodInfo, java.lang.String[])
     */
    public void startInline(FilterModule[] filterSet, MethodInfo method, String[] argReferences){
        this.currentMethod = method;

        inlineBlock = new Block();
        blockStack = new Stack();
        afterFilterBlocks = new Vector();
        labelTable = new Hashtable();
        currentLabelId = -1;

        empty = true;

        currentBlock = inlineBlock;
    }

    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#endInline()
     */
    public void endInline(){
        Enumeration afterFilters = afterFilterBlocks.elements();
        while( afterFilters.hasMoreElements() ){
            currentLabelId++;

            Block afterFilter = (Block) afterFilters.nextElement();
            afterFilter.getLabel().setId( currentLabelId );
            currentBlock.addInstruction( afterFilter );
        }
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#startFilter(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter, int)
     */
    public void startFilter(Filter filter, int jumpLabel){
        Block filterBlock;

        currentLabelId = jumpLabel;

        if( filter.getFilterType().getType().equals( FilterType.META ) ){
            Block block = new Block();
            block.setLabel( new Label( jumpLabel ) );
            currentBlock.addInstruction( block );

            filterBlock = new Block();
            filterBlock.setLabel( new Label() );
            afterFilterBlocks.add( filterBlock );
        }
        else{
            filterBlock = new Block();
            filterBlock.setLabel( new Label( jumpLabel ) );
            currentBlock.addInstruction( filterBlock );
        }

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
        if ( labelId != currentLabelId + 1 ){
            Label label = getLabel( labelId );

            Jump jump = new Jump( label );

            this.currentBlock.addInstruction( jump );
        }
    }


    /**
     * @see Composestar.Core.INLINE.lowlevel.LowLevelInlineStrategy#generateAction(Composestar.Core.FIRE2.model.ExecutionState)
     */
    public void generateAction(ExecutionState state){
        FilterAction filterAction;

        FlowNode node = state.getFlowNode();
        if ( node.containsName( FlowChartNames.DISPATCH_ACTION_NODE ) ){
            Message callMessage = getCallMessage( state );

            setInnerCallContext( callMessage );

            filterAction = new FilterAction( "dispatch", callMessage );

            Target target = callMessage.getTarget();
            MessageSelector selector = callMessage.getSelector();
            if ( !Message.checkEquals( Message.INNER_TARGET, target )
                    ||  !selector.getName().equals( 
                            currentMethod.name() ) )
            {
                empty = false;
            }
        }
        else if ( node.containsName( FlowChartNames.META_ACTION_NODE ) ){//"before action" ) ){
            Message callMessage = getCallMessage( state );

            setInnerCallContext( callMessage );

            filterAction = new FilterAction( "before", callMessage );
            empty = false;
        }
        else if ( node.containsName( "after action" ) ){
            Message callMessage = getCallMessage( state );

            setInnerCallContext( callMessage );

            filterAction = new FilterAction( "meta", callMessage );
            empty = false;
        }
        else if ( node.containsName( "skip action" ) ){
            Message callMessage = getCallMessage( state );

            setInnerCallContext( callMessage );

            filterAction = new FilterAction( "meta", callMessage );
            empty = false;
        }
        else if ( node.containsName( FlowChartNames.ERROR_ACTION_NODE ) ){
            filterAction = new FilterAction( "error", null );
            empty = false;
        }
        else if ( node.containsName( FlowChartNames.CONTINUE_ACTION_NODE ) ){
            filterAction = new FilterAction( "continue", null );
            empty = false;
        }
        else if ( node.containsName( FlowChartNames.SUBSTITUTION_ACTION_NODE ) ){
            filterAction = new FilterAction( "continue", null );
            empty = false;
        }
        else if ( node.containsName( FlowChartNames.CUSTOM_ACTION_NODE ) ){
            filterAction = new FilterAction( "custom", null );
        }
        else if ( node.containsName( "##SKIP##" ) ){
            //TODO correct flowchartname

            if ( lastAfterFilter != null ){
                Label label = lastAfterFilter.getLabel();
                Jump jump = new Jump( label );
                currentBlock.addInstruction( jump );
            }
            else{
                //jump to end:
                jump( -1 );
            }

            return;
        }
        else{
            throw new RuntimeException( "Unknown action" );
        }

        currentBlock.addInstruction( filterAction );
    }

    /**
     * Checks whether the call is an innercall and whether the called method has inlined filters. Then
     * the innercall filtercontext needs to be set.
     * @param state
     */
    private void setInnerCallContext( Message callMessage ){
        if ( Message.checkEquals( callMessage.getTarget(), Message.INNER_TARGET ) )
        {
            MethodInfo calledMethod;

            if ( callMessage.getSelector().getName().equals( currentMethod.name() ) ){
                calledMethod = currentMethod;
            }
            else{
                calledMethod = null;//TODO MethodInfo.getMethodInfo( callMessage.getSelector().getName(), 
                        //currentMethod.parent(), currentMethod );
            }

            ContextInstruction contextAction = 
                new ContextInstruction( ContextInstruction.SET_INNER_CALL, calledMethod );
            currentBlock.addInstruction( contextAction );
            builder.addInnerCallCheckTask( contextAction );
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

}
