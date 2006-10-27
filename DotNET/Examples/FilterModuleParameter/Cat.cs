using System;

namespace FilterModuleParameter
{
	/// <summary>
	/// Summary description for Cat.
	/// </summary>
	public class Cat
	{
		public Cat()
		{
		}

		public void feed()
		{
			Console.Out.WriteLine("eating catfood");
		}

		public void makeNoise()
		{
			Console.Out.WriteLine("meow");
		}

		// needed to make it compile with .NET 2.0
		public void walk() { }
	}
}
