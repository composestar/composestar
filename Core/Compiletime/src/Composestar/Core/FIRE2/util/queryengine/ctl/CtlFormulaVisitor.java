/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import Composestar.Core.FIRE2.util.queryengine.Predicate;

public interface CtlFormulaVisitor
{
	Object visitNot(Not formula, Object arg);

	Object visitAnd(And formula, Object arg);

	Object visitOr(Or formula, Object arg);

	Object visitImplies(Implies formula, Object arg);

	Object visitAX(AX formula, Object arg);

	Object visitEX(EX formula, Object arg);

	Object visitAF(AF formula, Object arg);

	Object visitEF(EF formula, Object arg);

	Object visitAG(AG formula, Object arg);

	Object visitEG(EG formula, Object arg);

	Object visitAU(AU formula, Object arg);

	Object visitEU(EU formula, Object arg);

	Object visitReverse(Reverse formula, Object arg);

	Object visitPredicate(Predicate predicate, Object arg);
}
