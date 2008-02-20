using System;

namespace Composestar.Patterns.TemplateMethod.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class ConcreteClass : System.Attribute
	{
		public ConcreteClass(){}
	}
}

