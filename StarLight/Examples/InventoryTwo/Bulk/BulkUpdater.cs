using System;
using System.Collections;

namespace InventoryTwo
{
	/// <summary>
	/// Internal for updating inventory with an array of products.
	/// </summary>
	public class BulkUpdater
	{
		public BulkUpdater()
		{
		}

		public void AddProducts(IEnumerable ps)
		{
			Inventory inv = (Inventory)(object)this;
			foreach (Product p in ps)
			{
				inv.AddProduct(p);
			}
		}

		public void RemoveProducts(IEnumerable ps)
		{
			Inventory inv = (Inventory)(object)this;
			foreach (Product p in ps)
			{
				inv.RemoveProduct(p);
			}
		}
	}
}
