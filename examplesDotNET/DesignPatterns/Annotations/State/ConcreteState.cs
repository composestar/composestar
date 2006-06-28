using System;

namespace Composestar.Patterns.State.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteState : System.Attribute
	{
		public ConcreteState(){}
	}
}

