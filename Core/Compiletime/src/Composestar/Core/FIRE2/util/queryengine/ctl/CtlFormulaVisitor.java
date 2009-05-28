/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import Composestar.Core.FIRE2.util.queryengine.Predicate;

public interface CtlFormulaVisitor<T>
{
	T visitNot(Not not, T arg);

	T visitAnd(And and, T arg);

	T visitOr(Or or, T arg);

	T visitImplies(Implies implies, T arg);

	T visitAX(AX ax, T arg);

	T visitEX(EX ex, T arg);

	T visitAF(AF af, T arg);

	T visitEF(EF ef, T arg);

	T visitAG(AG ag, T arg);

	T visitEG(EG eg, T arg);

	T visitAU(AU au, T arg);

	T visitEU(EU eu, T arg);

	T visitReverse(Reverse reverse, T arg);

	T visitPredicate(Predicate predicate, T arg);
}
