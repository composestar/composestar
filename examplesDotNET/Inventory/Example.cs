using System;

namespace ExampleAOP
{
	/// <summary>
	/// Example application for keeping a bunch of products in an inventory.
	/// </summary>
	class Example
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			Inventory i = new Inventory();
			InventoryDisplay id = new InventoryDisplay(i);
			InventoryCount ic = new InventoryCount(i);

			Product p = new Product("P");
			i.AddProduct(new Product("A"));
			i.AddProduct(p);
			i.AddProduct(new Product("Z"));
			i.RemoveProduct(p);
		}
	}
}
