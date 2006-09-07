using System;

namespace Composestar.Patterns.Singleton.Annotations
{
	[System.AttributeUsage(System.AttributeTargets.Class)]
	public class Singleton : System.Attribute
	{
		public Singleton(){}
	}
}

