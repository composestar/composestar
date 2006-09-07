using System;

namespace Composestar.Patterns.ChainOfResponsibility.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteHandler : System.Attribute
	{
		public ConcreteHandler(){}
	}
}

