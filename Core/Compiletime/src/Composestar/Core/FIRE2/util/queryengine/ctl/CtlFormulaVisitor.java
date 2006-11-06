/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import Composestar.Core.FIRE2.util.queryengine.Predicate;

public interface CtlFormulaVisitor
{
	public Object visitNot(Not formula, Object arg);

	public Object visitAnd(And formula, Object arg);

	public Object visitOr(Or formula, Object arg);

	public Object visitImplies(Implies formula, Object arg);

	public Object visitAX(AX formula, Object arg);

	public Object visitEX(EX formula, Object arg);

	public Object visitAF(AF formula, Object arg);

	public Object visitEF(EF formula, Object arg);

	public Object visitAG(AG formula, Object arg);

	public Object visitEG(EG formula, Object arg);

	public Object visitAU(AU formula, Object arg);

	public Object visitEU(EU formula, Object arg);

	public Object visitReverse(Reverse formula, Object arg);

	public Object visitPredicate(Predicate predicate, Object arg);
}
