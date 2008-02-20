using System;

namespace Composestar.Patterns.Iterator.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteIterator : System.Attribute
	{
		public ConcreteIterator(){}
	}
}

