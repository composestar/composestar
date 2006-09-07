using System;
using System.Collections;

namespace ExampleAOP
{
	/// <summary>
	/// Count the number of products in inventory.
	/// </summary>
	public class InventoryCount
	{
		private Inventory inventory;

		public InventoryCount(Inventory i)
		{
			inventory = i;
		}

		public void CountInventory()
		{
			int count = 0;
			IEnumerator iter = inventory.GetInventory();
			while (iter.MoveNext())
			{
				count++;
			}
			Console.WriteLine("Number of Products in Inventory: {0}", count);
		}
	}
}
