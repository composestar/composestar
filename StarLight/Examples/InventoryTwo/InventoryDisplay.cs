using System;
using System.Collections;

namespace InventoryTwo
{
	/// <summary>
	/// Shows a list of products in inventory.
	/// </summary>
	public class InventoryDisplay : IObserver
	{
		private Inventory inventory;

		public InventoryDisplay(Inventory inventory)
		{
			this.inventory = inventory;
		}

		public void Update(/*Object s*/)
		{
			/*Inventory inventory = (Inventory)s;*/
			Console.WriteLine("List of products in Inventory:");
			foreach (Product p in inventory.GetProducts())
			{
				Console.WriteLine(p.Name);
			}
		}
	}
}
