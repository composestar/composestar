using System;

namespace Composestar.Patterns.FactoryMethod.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteCreator : System.Attribute
	{
		public ConcreteCreator(){}
	}
}

