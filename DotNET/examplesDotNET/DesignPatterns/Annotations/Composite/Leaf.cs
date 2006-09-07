using System;

namespace Composestar.Patterns.Composite.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class Leaf : System.Attribute
	{
		public Leaf(){}
	}
}

