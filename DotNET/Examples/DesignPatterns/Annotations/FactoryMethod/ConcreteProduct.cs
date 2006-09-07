using System;

namespace Composestar.Patterns.FactoryMethod.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteProduct : System.Attribute
	{
		public ConcreteProduct(){}
	}
}

