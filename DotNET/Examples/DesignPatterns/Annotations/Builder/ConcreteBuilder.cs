using System;

namespace Composestar.Patterns.Builder.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteBuilder : System.Attribute
	{
		public ConcreteBuilder(){}
	}
}

