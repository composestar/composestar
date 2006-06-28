using System;

namespace Composestar.Patterns.Bridge.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class Implementor: System.Attribute
	{
		public Implementor(){}
	}
}

