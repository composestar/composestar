using System;

namespace Composestar.Patterns.Visitor.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteVisitor : System.Attribute
	{
		public ConcreteVisitor(){}
	}
}

