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

        void VisitWhile(WhileInstruction whileInstr);
        void VisitWhileEnd(WhileInstruction whileInstr);

        void VisitSwitch(SwitchInstruction switchInstr);
        void VisitCase(CaseInstruction caseInstr);
        void VisitCaseEnd(SwitchInstruction switchInstr);
        void VisitSwitchEnd(SwitchInstruction switchInstr);

        void VisitCreateActionStore(ContextInstruction contextInstruction);
        void VisitStoreAction(ContextInstruction contextInstruction);

        void VisitCreateJoinPointContext(ContextInstruction contextInstruction);
        void VisitRestoreJoinPointContext(ContextInstruction contextInstruction);
    } // IVisitor
} // namespace Composestar.StarLight.Entities.WeaveSpec.Instructions.Visitor
