using System;

namespace ExampleAOP
{
	/// <summary>
	/// Interface defining the update facility for any kind of observer internal.
	/// </summary>
	public interface Observer
	{
		 void Update(Subject s);
	}
}
