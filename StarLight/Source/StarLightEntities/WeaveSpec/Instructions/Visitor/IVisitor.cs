using Composestar.StarLight.Entities.WeaveSpec.Instructions;

namespace Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor
{

    /// <summary>
    /// Visitor for the instructions.
    /// </summary>    
    public interface IVisitor
    {
        void VisitBranch(Branch branch);
        void VisitBranchFalse(Branch branch);
        void VisitBranchEnd(Branch branch);

        void VisitCheckInnerCall(ContextInstruction contextInstruction);
        void VisitSetInnerCall(ContextInstruction contextInstruction);
        void VisitResetInnerCall(ContextInstruction contextInstruction);
        void VisitReturnAction(ContextInstruction contextInstruction);

        void VisitFilterAction(FilterAction filterAction);
        void VisitJumpInstruction(JumpInstruction jump);

        void VisitInlineInstruction(InlineInstruction inlineInstruction);

        void VisitWhile(WhileInstruction whileInstruction);
        void VisitWhileEnd(WhileInstruction whileInstruction);

        void VisitSwitch(SwitchInstruction switchInstruction);
        void VisitCase(CaseInstruction caseInstruction);
        void VisitCaseEnd(SwitchInstruction switchInstruction);
        void VisitSwitchEnd(SwitchInstruction switchInstruction);

        void VisitCreateActionStore(ContextInstruction contextInstruction);
        void VisitStoreAction(ContextInstruction contextInstruction);

        void VisitCreateJoinPointContext(ContextInstruction contextInstruction);
        void VisitRestoreJoinPointContext(ContextInstruction contextInstruction);
    } // IVisitor
} // namespace Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor
