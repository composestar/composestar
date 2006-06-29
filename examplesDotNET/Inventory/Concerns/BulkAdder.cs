using System;
using System.Collections;

namespace ExampleAOP
{
	/// <summary>
	/// Internal for adding an array of products.
	/// </summary>
	public class BulkAdder
	{
		public BulkAdder()
		{
		}

		public void AddProducts(ArrayList ps)
		{
			foreach(Product p in ps)
			{
				((Inventory)(object)this).AddProduct(p);
			}
		}
	}
}
