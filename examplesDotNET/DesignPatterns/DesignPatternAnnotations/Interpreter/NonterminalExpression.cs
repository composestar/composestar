using System;

namespace Composestar.Patterns.Interpreter.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class NonterminalExpression : System.Attribute
	{
		public NonterminalExpression(){}
	}
}

