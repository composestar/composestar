using System;

namespace BasicTests
{
	public class FooBarQuux: TestsBase
	{
		public void foo()
		{
			report("foo");
		}

		public void bar()
		{
			report("bar");
		}

		public void quux()
		{
			report("quux");
		}

		public void success()
		{
			report("success");
		}
	}
}