using System;

namespace GrammarTest
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	class Class1
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			System.Console.WriteLine("Begin of test.......");
			Foo foo = new Foo();
			OuterFoo outerfoo = OuterFoo.instance();
			Bar bar = new Bar();
			foo.write();
			outerfoo.write();
			ReusedFoo reusedFoo = new ReusedFoo(foo);
			System.Console.WriteLine("End of test.........");
			//System.Console.WriteLine("End, press any key to continue");
			//System.Console.ReadLine();
			
		}
	}
}
