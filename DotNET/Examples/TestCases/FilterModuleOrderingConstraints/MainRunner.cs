using System;

namespace FilterModuleOrderingConstraints
{
	public class MainRunner
	{
		public static void Main(string[] args)
		{
			Console.WriteLine("---------------------");
			MainRunner mr = new MainRunner();
            Console.WriteLine("---------------------");
		}

		public MainRunner()
		{
			Foo foo = new Foo();
			foo.sayHello();
		}
	}
}