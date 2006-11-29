using System;

namespace InventoryTwo
{
	/// <summary>
	/// Interface defining the update facility for any kind of observer internal.
	/// </summary>
	public interface IObserver
	{
		void Update(Subject s);
	}
}
