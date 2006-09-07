using System;

namespace Composestar.Patterns.Interpreter.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class TerminalExpression : System.Attribute
	{
		public TerminalExpression(){}
	}
}

