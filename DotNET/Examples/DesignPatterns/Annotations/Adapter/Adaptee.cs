using System;

namespace Composestar.Patterns.Adapter.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class Adaptee : System.Attribute
	{
		public Adaptee(){}
	}
}

