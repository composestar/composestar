using System;

namespace SECRETTesting
{
	public class MainRunner
	{
		public static void Main(string[] args)
		{
			MainRunner mr = new MainRunner();
		}

		public MainRunner()
		{
			Console.Out.WriteLine("> " + this.a(1) );
		}

		public int a(int a)
		{
			return a+1;
		}
	}
}