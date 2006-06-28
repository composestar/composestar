using System;

namespace Composestar.Patterns.Decorator.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteDecorator : System.Attribute
	{
		public ConcreteDecorator(){}
	}
}

