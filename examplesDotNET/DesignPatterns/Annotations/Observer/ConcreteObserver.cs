using System;

namespace Composestar.Patterns.Observer.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteObserver : System.Attribute
	{
		public ConcreteObserver(){}
	}
}

