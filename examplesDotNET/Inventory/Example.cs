using System;
using System.Collections;

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
			InventoryCount ic = new InventoryCount(i);
			InventoryDisplay id = new InventoryDisplay(i);

			Product p = new Product("P");
			i.AddProduct(new Product("A"));
			i.AddProduct(p);
			i.AddProduct(new Product("Z"));
			i.RemoveProduct(p);

			ArrayList ps = new ArrayList();
			ps.Add(new Product("L1"));
			ps.Add(new Product("L2"));
			ps.Add(new Product("L3"));
			i.AddProducts(ps);
		}
	}
}
