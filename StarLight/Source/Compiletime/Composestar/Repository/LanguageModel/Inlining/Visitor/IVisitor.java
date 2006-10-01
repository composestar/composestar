package Composestar.Repository.LanguageModel.Inlining.Visitor;

import Composestar.Repository.LanguageModel.Inlining.*;
   
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
	void VisitContinueAction(FilterAction filterAction);
	void VisitSubstitutionAction(FilterAction filterAction);
	void VisitErrorAction(FilterAction filterAction);
	void VisitDispatchAction(FilterAction filterAction);
	void VisitBeforeAction(FilterAction filterAction);
	void VisitAfterAction(FilterAction filterAction);
	void VisitSkipAction(FilterAction filterAction);
	void VisitJumpInstruction(Jump jump);

	void VisitInlineInstruction(InlineInstruction inlineInstruction);

    void VisitWhile(While whileInstr);
	void VisitWhileEnd(While whileInstr);

    void VisitSwitch(Switch switchInstr);
	void VisitCase(Case caseInstr);
	void VisitCaseEnd(Switch switchInstr);
	void VisitSwitchEnd(Switch switchInstr);

	void VisitCreateActionStore(ContextInstruction contextInstruction);
	void VisitStoreAction(ContextInstruction contextInstruction);

}
