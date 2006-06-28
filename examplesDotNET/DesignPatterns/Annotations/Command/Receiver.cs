using System;

namespace Composestar.Patterns.Command.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class Receiver : System.Attribute
	{
		public Receiver(){}
	}
}

