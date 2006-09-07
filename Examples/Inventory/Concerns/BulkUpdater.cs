using System;
using System.Collections;

namespace ExampleAOP
{
	/// <summary>
	/// Internal for updating inventory with an array of products.
	/// </summary>
	public class BulkUpdater
	{
		public BulkUpdater()
		{
		}

		public void AddProducts(ArrayList ps)
		{
			foreach(Product p in ps)
			{
				((Inventory)(object)this).AddProduct(p);
			}
		}

		public void RemoveProducts(ArrayList ps)
		{
			foreach(Product p in ps)
			{
				((Inventory)(object)this).RemoveProduct(p);
			}
		}
	}
}
