/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.util.queryengine.Predicate;
import Composestar.Core.FIRE2.util.queryengine.Query;

public interface CtlFormula extends Query
{
	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg);
}

final class Not implements CtlFormula
{
	public CtlFormula subFormula;

	public Not(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitNot(this, arg);
	}

	@Override
	public String toString()
	{
		return "Not";
	}
}

final class And implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public And(CtlFormula sub1, CtlFormula sub2)
	{
		subFormula1 = sub1;
		subFormula2 = sub2;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitAnd(this, arg);
	}

	@Override
	public String toString()
	{
		return "And";
	}
}

final class Or implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public Or(CtlFormula sub1, CtlFormula sub2)
	{
		subFormula1 = sub1;
		subFormula2 = sub2;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitOr(this, arg);
	}

	@Override
	public String toString()
	{
		return "Or";
	}
}

final class Implies implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public Implies(CtlFormula sub1, CtlFormula sub2)
	{
		subFormula1 = sub1;
		subFormula2 = sub2;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitImplies(this, arg);
	}

	@Override
	public String toString()
	{
		return "Implies";
	}
}

final class AX implements CtlFormula
{
	public CtlFormula subFormula;

	public AX(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitAX(this, arg);
	}

	@Override
	public String toString()
	{
		return "AX";
	}
}

final class EX implements CtlFormula
{
	public CtlFormula subFormula;

	public EX(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitEX(this, arg);
	}

	@Override
	public String toString()
	{
		return "EX";
	}
}

final class AF implements CtlFormula
{
	public CtlFormula subFormula;

	public AF(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitAF(this, arg);
	}

	@Override
	public String toString()
	{
		return "AF";
	}
}

final class EF implements CtlFormula
{
	public CtlFormula subFormula;

	public EF(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitEF(this, arg);
	}

	@Override
	public String toString()
	{
		return "EF";
	}
}

final class AG implements CtlFormula
{
	public CtlFormula subFormula;

	public AG(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitAG(this, arg);
	}

	@Override
	public String toString()
	{
		return "AG";
	}
}

final class EG implements CtlFormula
{
	public CtlFormula subFormula;

	public EG(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitEG(this, arg);
	}

	@Override
	public String toString()
	{
		return "EG";
	}
}

final class AU implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public AU(CtlFormula sub1, CtlFormula sub2)
	{
		subFormula1 = sub1;
		subFormula2 = sub2;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitAU(this, arg);
	}

	@Override
	public String toString()
	{
		return "AU";
	}
}

final class EU implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public EU(CtlFormula sub1, CtlFormula sub2)
	{
		subFormula1 = sub1;
		subFormula2 = sub2;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitEU(this, arg);
	}

	@Override
	public String toString()
	{
		return "EU";
	}
}

final class Reverse implements CtlFormula
{
	public CtlFormula subFormula;

	public Reverse(CtlFormula sub)
	{
		subFormula = sub;
	}

	public <T> T visit(CtlFormulaVisitor<T> visitor, T arg)
	{
		return visitor.visitReverse(this, arg);
	}

	@Override
	public String toString()
	{
		return "Reverse";
	}
}

final class True extends Predicate
{
	@Override
	public boolean isTrue(ExecutionState state)
	{
		return true;
	}

	@Override
	public String toString()
	{
		return "True";
	}
}

final class False extends Predicate
{
	@Override
	public boolean isTrue(ExecutionState state)
	{
		return false;
	}

	@Override
	public String toString()
	{
		return "False";
	}
}
