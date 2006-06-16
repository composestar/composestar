using System;
using System.Collections;

namespace ExampleAOP
{
	/// <summary>
	/// Keep a list of products in inventory.
	/// </summary>
	public class Inventory
	{
		private ArrayList stock;

		public Inventory()
		{
			stock = new ArrayList();
		}

		public void AddProduct(Product p) 
		{
			stock.Add(p);
		}

		public void RemoveProduct(Product p)
		{
			stock.Remove(p);
		}

		public IEnumerator GetInventory()
		{
			return stock.GetEnumerator();
		}
	}
}
