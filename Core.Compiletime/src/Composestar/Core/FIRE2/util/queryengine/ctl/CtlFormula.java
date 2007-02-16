/*
 * Created on 19-jul-2006
 *
 */
package Composestar.Core.FIRE2.util.queryengine.ctl;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.util.queryengine.Predicate;

public interface CtlFormula
{
	public Object visit(CtlFormulaVisitor visitor, Object arg);
}

final class Not implements CtlFormula
{
	public CtlFormula subFormula;

	public Not(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitNot(this, arg);
	}

	public String toString()
	{
		return "Not";
	}
}

final class And implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public And(CtlFormula subFormula1, CtlFormula subFormula2)
	{
		this.subFormula1 = subFormula1;
		this.subFormula2 = subFormula2;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitAnd(this, arg);
	}

	public String toString()
	{
		return "And";
	}
}

final class Or implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public Or(CtlFormula subFormula1, CtlFormula subFormula2)
	{
		this.subFormula1 = subFormula1;
		this.subFormula2 = subFormula2;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitOr(this, arg);
	}

	public String toString()
	{
		return "Or";
	}
}

final class Implies implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public Implies(CtlFormula subFormula1, CtlFormula subFormula2)
	{
		this.subFormula1 = subFormula1;
		this.subFormula2 = subFormula2;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitImplies(this, arg);
	}

	public String toString()
	{
		return "Implies";
	}
}

final class AX implements CtlFormula
{
	public CtlFormula subFormula;

	public AX(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitAX(this, arg);
	}

	public String toString()
	{
		return "AX";
	}
}

final class EX implements CtlFormula
{
	public CtlFormula subFormula;

	public EX(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitEX(this, arg);
	}

	public String toString()
	{
		return "EX";
	}
}

final class AF implements CtlFormula
{
	public CtlFormula subFormula;

	public AF(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitAF(this, arg);
	}

	public String toString()
	{
		return "AF";
	}
}

final class EF implements CtlFormula
{
	public CtlFormula subFormula;

	public EF(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitEF(this, arg);
	}

	public String toString()
	{
		return "EF";
	}
}

final class AG implements CtlFormula
{
	public CtlFormula subFormula;

	public AG(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitAG(this, arg);
	}

	public String toString()
	{
		return "AG";
	}
}

final class EG implements CtlFormula
{
	public CtlFormula subFormula;

	public EG(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitEG(this, arg);
	}

	public String toString()
	{
		return "EG";
	}
}

final class AU implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public AU(CtlFormula subFormula1, CtlFormula subFormula2)
	{
		this.subFormula1 = subFormula1;
		this.subFormula2 = subFormula2;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitAU(this, arg);
	}

	public String toString()
	{
		return "AU";
	}
}

final class EU implements CtlFormula
{
	public CtlFormula subFormula1;

	public CtlFormula subFormula2;

	public EU(CtlFormula subFormula1, CtlFormula subFormula2)
	{
		this.subFormula1 = subFormula1;
		this.subFormula2 = subFormula2;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitEU(this, arg);
	}

	public String toString()
	{
		return "EU";
	}
}

final class Reverse implements CtlFormula
{
	public CtlFormula subFormula;

	public Reverse(CtlFormula subFormula)
	{
		this.subFormula = subFormula;
	}

	public Object visit(CtlFormulaVisitor visitor, Object arg)
	{
		return visitor.visitReverse(this, arg);
	}

	public String toString()
	{
		return "Reverse";
	}
}

final class True extends Predicate
{
	public boolean isTrue(ExecutionState state)
	{
		return true;
	}

	public String toString()
	{
		return "True";
	}
}

final class False extends Predicate
{
	public boolean isTrue(ExecutionState state)
	{
		return false;
	}

	public String toString()
	{
		return "False";
	}
}
