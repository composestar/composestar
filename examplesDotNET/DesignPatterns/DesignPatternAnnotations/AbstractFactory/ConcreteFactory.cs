using System;

namespace Composestar.Patterns.AbstractFactory.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteFactory : System.Attribute
	{
		public ConcreteFactory(){}
	}
}

