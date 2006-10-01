package Composestar.Repository.LanguageModel.ConditionExpressions.Visitor;

import Composestar.Repository.LanguageModel.ConditionExpressions.*;
   
public interface IVisitor
{
	void VisitAnd(And and);
	void VisitConditionLiteral(ConditionLiteral conditionLiteral);
	void VisitFalse(False f);
	void VisitNot(Not not);
	void VisitOr(Or or);
	void VisitTrue(True t);
}
