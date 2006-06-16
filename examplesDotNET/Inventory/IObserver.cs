using System;

namespace ExampleAOP
{
	/// <summary>
	/// Interface defining the update facility for any kind of observer internal.
	/// </summary>
	public abstract class IObserver
	{
		public abstract void Update(Subject s);
	}
}
