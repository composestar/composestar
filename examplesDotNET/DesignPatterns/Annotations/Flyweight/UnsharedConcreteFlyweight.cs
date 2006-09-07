using System;

namespace Composestar.Patterns.Flyweight.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class UnsharedConcreteFlyweight : System.Attribute
	{
		public UnsharedConcreteFlyweight(){}
	}
}

