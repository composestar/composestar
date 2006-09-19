package Composestar.Repository.LanguageModel.Inlining.Visitor;

import Composestar.Repository.LanguageModel.Inlining.*;
   
public interface IVisitor
{
	
	void VisitBranch(Branch branch);
	void VisitContextInstruction(ContextInstruction contextInstruction);
	void VisitFilterAction(FilterAction filterAction);
	void VisitContinueAction(FilterAction filterAction);
	void VisitSubstitutionAction(FilterAction filterAction);
	void VisitErrorAction(FilterAction filterAction);
	void VisitDispatchAction(FilterAction filterAction);
	void VisitBeforeAction(FilterAction filterAction);
	void VisitAfterAction(FilterAction filterAction);
	void VisitSkipAction(FilterAction filterAction);
	void VisitJumpInstruction(Jump jump);
	
}
