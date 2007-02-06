using System;

namespace Wedding
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

			Console.WriteLine(man.ToString());
			Console.WriteLine(woman.ToString());

			man.PreposeTo(woman);

			Console.WriteLine("So in the end");
            Console.WriteLine(man.GetMarriedStatus());
            Console.WriteLine(woman.GetMarriedStatus());
		}
	}
}
