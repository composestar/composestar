using System;

namespace InventoryTwo
{
	/// <summary>
	/// A product.
	/// </summary>
	public class Product
	{
		private string name;

		public Product(string n)
		{
			name = n;
		}

		public string Name
		{
			get { return name; }
		}
	}
}
