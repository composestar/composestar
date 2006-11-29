using System;

namespace InventoryTwo
{
	[AttributeUsage(AttributeTargets.Method)]
	public class Observed : Attribute
	{
		public Observed()
		{
		}
	}
}
