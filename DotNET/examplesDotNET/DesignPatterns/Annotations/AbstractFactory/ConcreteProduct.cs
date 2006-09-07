using System;

namespace Composestar.Patterns.AbstractFactory.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteProduct : System.Attribute
	{
		public ConcreteProduct(){}
	}
}

