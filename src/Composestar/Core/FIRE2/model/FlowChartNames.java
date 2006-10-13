/*
 * Created on 16-aug-2006
 *
 */
package Composestar.Core.FIRE2.model;

public interface FlowChartNames {
    //nodes:
    public final static String FLOW_ELEMENT_NODE = "FlowElement";
    
    
    public final static String FILTER_MODULE_NODE = "FilterModule";
    public final static String FILTER_NODE = "Filter";
    public final static String FILTER_ELEMENT_NODE = "FilterElement";
    public final static String MATCHING_PATTERN_NODE = "MatchingPattern";
    public final static String MATCHING_PART_NODE = "MatchingPart";
    public final static String SUBSTITUTION_PART_NODE = "SubstitutionPart";
    
    public final static String CONDITION_OPERATOR_NODE = "ConditionOperator";
    public final static String DISABLE_OPERATOR_NODE = "DisableOperator";
    public final static String ENABLE_OPERATOR_NODE = "EnableOperator";
    
    
    public final static String CONDITION_EXPRESSION_NODE = "ConditionExpression";
    public final static String TRUE_NODE = "True";
    public final static String FALSE_NODE = "False";
    
    public final static String ACTION_NODE = "FilterAction";
    public final static String FILTER_ACTION_NODE = "FilterAction";
    public final static String CONTINUE_ACTION_NODE = "ContinueFlowAction";
    public final static String EXIT_ACTION_NODE = "ExitFlowAction";
    public final static String RETURN_ACTION_NODE = "ReturnFlowAction";
    public final static String ORIGINAL_MESSAGE_ACTION_NODE = "OriginalMessageAction";
    public final static String SUBSTITUTED_MESSAGE_ACTION_NODE = "SubstitutedMessageAction";
    public final static String ANY_MESSAGE_ACTION_NODE = "AnyMessageAction";
    public final static String REJECT_CALL_ACTION_NODE = "RejectCallAction";
    public final static String ACCEPT_CALL_ACTION_NODE = "AcceptCallAction";
    public final static String REJECT_RETURN_ACTION_NODE = "RejectReturnAction";
    public final static String ACCEPT_RETURN_ACTION_NODE = "AcceptReturnAction";
    
    public final static String SIGNATURE_MATCHING_NODE = "SignatureMatchingPart";
    public final static String NAME_MATCHING_NODE = "NameMatchingPart";
    
    public final static String FILTER_COMP_OPER_NODE = "FilterCompOper";
    public final static String SEQ_FILTER_COMP_OPER_NODE = "SEQFilterCompOper";
    public final static String VOID_FILTER_COMP_OPER_NODE = "VoidFilterCompOper";
    
    public final static String FE_COMP_OPER_NODE = "CompOper";
    public final static String FE_COR_COMP_OPER_NODE = "CORCompOper";
    public final static String FE_VOID_COMP_OPER_NODE = "VoidCompOper";
    
    /**
     * The end node marks the end of the flow in the current filtermodule
     * and the continuation to the next filtermodule.
     */
    public final static String END_NODE = "End";
    
    /**
     * The stop node marks the exit of the filterset
     */
    public final static String STOP_NODE = "Stop";
    
    /**
     * The return node marks the return through the filterset
     */
    public final static String RETURN_NODE = "Return";
    
    public final static String CONTEXT_NODE = "ContextNode";
    public final static String PROCEDURE_NODE = "ProcedureNode";
    public final static String PREDICATE_NODE = "PredicateNode";
    
    
    //edges:
    
    public final static String FILTER_EDGE = "filter";
    public final static String ORDER_FIRST_EDGE = "orderFirst";
    public final static String ORDER_NEXT_EDGE = "orderNext";
    public final static String RIGHT_OPERATOR_EDGE = "rightOper";
    public final static String RIGHT_ARGUMENT_EDGE = "rightArg";
    
    public final static String REJECT_CALL_EDGE = "rejectCall";
    public final static String ACCEPT_CALL_EDGE = "acceptCall";
    public final static String REJECT_RETURN_EDGE = "rejectReturn";
    public final static String ACCEPT_RETURN_EDGE = "acceptReturn";
    
    public final static String FILTER_ELEMENT_EDGE = "filterElement";
    
    public final static String CONDITION_PART_EDGE = "conditionPart";
    public final static String CONDITION_OPERATOR_EDGE = "conditionOperator";
    public final static String MATCHING_PATTERN_EDGE = "matchingPattern";
    
    public final static String MATCHING_PART_EDGE = "matchingPart";
    public final static String SUBSTITUTION_PART_EDGE = "substitutionPart";
    
    public final static String SELECTOR_EDGE = "selector";
    public final static String TARGET_EDGE = "target";
}