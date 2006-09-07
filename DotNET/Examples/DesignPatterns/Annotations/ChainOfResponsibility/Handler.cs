using System;

namespace Composestar.Patterns.ChainOfResponsibility.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class Handler : System.Attribute
	{
		public Handler(){}
	}
}

