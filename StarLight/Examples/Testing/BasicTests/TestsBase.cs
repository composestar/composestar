using System;

namespace BasicTests
{
	public class TestsBase
	{
		public TestsBase()
		{
			report("ctor");
		}

		public void report(Object arg)
		{
			Console.Out.WriteLine("{0}.{1}()", this, arg);
		}
	}
}
