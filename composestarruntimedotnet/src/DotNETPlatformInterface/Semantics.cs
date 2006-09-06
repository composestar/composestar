using System;

namespace Composestar.RuntimeDotNET
{
	[AttributeUsage(AttributeTargets.Method, Inherited = false, AllowMultiple = false)]
	public sealed class Semantics: Attribute
	{
		public string description;

		public Semantics(string description) 
		{
			this.description = description;
		}
	}
}