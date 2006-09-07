using System;

namespace JPInfo
{
	public class JoinPointInfoConcern
	{
		public JoinPointInfoConcern(Foo foo, Bar bar)
		{

		}

		public void sayHello()
		{
			Console.WriteLine("Hello");
		}
	}
}