using System;

namespace Composestar.Patterns.Proxy.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class)]
	public class RealSubject : System.Attribute
	{
		public RealSubject(){}
	}
}

