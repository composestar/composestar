using System;

namespace Composestar.Patterns.TemplateMethod.Annotations
{
	[System.AttributeUsage (System.AttributeTargets.Class  | System.AttributeTargets.Interface)]
	public class AbstractClass : System.Attribute
	{
		public AbstractClass(){}
	}
}

