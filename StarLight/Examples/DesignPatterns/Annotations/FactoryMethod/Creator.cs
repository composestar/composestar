using System;

namespace Composestar.Patterns.FactoryMethod.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class Creator : System.Attribute
	{
		public Creator(){}
	}
}

