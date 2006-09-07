using System;

namespace Composestar.Patterns.Decorator.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteComponent : System.Attribute
	{
		public ConcreteComponent(){}
	}
}

