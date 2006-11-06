/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.util.queryengine.ctl.CtlFormula;
import Composestar.Core.FIRE2.util.queryengine.ctl.CtlFormulaVisitor;

public abstract class Predicate implements CtlFormula
{
	public abstract boolean isTrue(ExecutionState state);

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitPredicate(this, arg);
	}

	public String toString()
	{
		return "Predicate";
	}
}
