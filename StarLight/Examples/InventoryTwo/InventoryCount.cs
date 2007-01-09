using System;
using System.Collections;

namespace InventoryTwo
{
	/// <summary>
	/// Displays the number of products in the inventory.
	/// </summary>
	public class InventoryCount : IObserver
	{
		private Inventory inventory;

		public InventoryCount(Inventory inventory)
		{
			this.inventory = inventory;
		}

		public void Update(/*Object s*/)
		{
			/*Inventory inventory = (Inventory)s;*/
			int count = 0;
			foreach (Product p in inventory.GetProducts())
			{
				count++;
			}
			Console.WriteLine("Number of products in Inventory: {0}", count);
		}
	}
}
