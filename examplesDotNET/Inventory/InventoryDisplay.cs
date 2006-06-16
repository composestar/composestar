using System;
using System.Collections;

namespace ExampleAOP
{
	/// <summary>
	/// Show a list of products in inventory.
	/// </summary>
	public class InventoryDisplay
	{
		private Inventory inventory;

		public InventoryDisplay(Inventory i)
		{
			inventory = i;
		}

		public void DisplayInventory()
		{
			Console.WriteLine("List of Products in Inventory:");
			IEnumerator iter = inventory.GetInventory();
			while (iter.MoveNext())
			{
				Console.WriteLine(((Product)iter.Current).Name);
			}
		}
	}
}
