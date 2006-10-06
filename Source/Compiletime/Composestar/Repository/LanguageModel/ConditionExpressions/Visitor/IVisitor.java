package Composestar.Repository.LanguageModel.ConditionExpressions.Visitor;

import Composestar.Repository.LanguageModel.ConditionExpressions.*;
   
public interface IVisitor
{
	void VisitAndLeft(And and);
	void VisitAndRight(And and);
	void VisitConditionLiteral(ConditionLiteral conditionLiteral);
	void VisitFalse(False f);
	void VisitTrue(True t);
	void VisitNot(Not not);
	void VisitOrLeft(Or or);
	void VisitOrRight(Or or);
	
}
