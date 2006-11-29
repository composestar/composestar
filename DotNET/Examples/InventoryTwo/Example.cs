using System;
using System.Collections;

namespace InventoryTwo
{
	/// <summary>
	/// Example application for keeping a bunch of products in an inventory.
	/// </summary>
	public class Example
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			Inventory inventory = new Inventory();
			inventory.Attach(new InventoryCount(inventory));
			inventory.Attach(new InventoryDisplay(inventory));
			
			Console.WriteLine("=Adding A=");
			inventory.AddProduct(new Product("A"));
			
			Console.WriteLine("=Adding P=");
			Product p = new Product("P");
			inventory.AddProduct(p);

			Console.WriteLine("=Adding Z=");
			inventory.AddProduct(new Product("Z"));

			Console.WriteLine("=Removing P=");
			inventory.RemoveProduct(p);

			ArrayList ps = new ArrayList();
			ps.Add(new Product("L1"));
			ps.Add(new Product("L2"));
			ps.Add(new Product("L3"));

			Console.WriteLine("=Adding L1, L2, L3=");
			inventory.AddProducts(ps);

			Console.WriteLine("=Removing L1, L2, L3=");
			inventory.RemoveProducts(ps);
		}
	}
}
