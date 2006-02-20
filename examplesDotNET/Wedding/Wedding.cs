using System;

namespace wedding
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	class Wedding
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			Person man = new Person("Linus Gates");
			Person woman = new Person("Belinda Torvalts");

			Console.WriteLine(man.toString());
			Console.WriteLine(woman.toString());

			man.preposeTo(woman);

			Console.WriteLine("So in the end");
			Console.WriteLine(man.toString());
			Console.WriteLine(woman.toString());
		}
	}
}
