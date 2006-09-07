using System;

namespace Composestar.Patterns.Bridge.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteImplementor: System.Attribute
	{
		public ConcreteImplementor(){}
	}
}

