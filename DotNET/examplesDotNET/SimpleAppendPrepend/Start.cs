using System;

namespace SimpleAppendPrepend
{
	public class Start
	{
		public void second() 
		{
			Console.WriteLine( "second" );
		}

		public static void Main(string[] args)
		{
			Console.WriteLine( "Simple append/prepend example" );
			Start s = new Start();
			s.second();
		}
	}
}
