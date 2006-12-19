using System;
using System.Collections;

namespace InventoryTwo
{
	/// <summary>
	/// Keep a list of products in inventory.
	/// </summary>
	public class Inventory
	{
		private IList stock;

		public Inventory()
		{
			stock = new ArrayList();
		}

		[Observed]
		public void AddProduct(Product p) 
		{
			stock.Add(p);
		}

		[Observed]
		public void RemoveProduct(Product p)
		{
			stock.Remove(p);
		}

		public IEnumerable GetProducts()
		{
			return stock;
		}
	}
}
