using System;

namespace JPInfo
{
	public class MainRunner
	{
		public static void Main(string[] args)
		{
			Console.WriteLine("Hey");
			MainRunner mr = new MainRunner();
            Console.WriteLine("---------------------");
            Console.WriteLine("press enter to finish");
            Console.ReadLine();
		}

		public MainRunner()
		{
			JoinPointInfoConcern jpic = new JoinPointInfoConcern(new Foo(), new Bar());
			jpic.sayHello();
		}
	}
}