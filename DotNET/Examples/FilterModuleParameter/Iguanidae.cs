using System;

namespace FilterModuleParameter
{
	/// <summary>
	/// Summary description for Iguanidae.
	/// </summary>
	public class Iguanidae
	{
		public Iguanidae()
		{
		}

		public void feed()
		{
			Console.Out.WriteLine("eating lettuce");
		}

		// needed to make it compile with .NET 2.0
		public void walk() { }
	}
}
