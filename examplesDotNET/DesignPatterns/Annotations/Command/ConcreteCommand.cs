using System;

namespace Composestar.Patterns.Command.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteCommand : System.Attribute
	{
		public ConcreteCommand(){}
	}
}

