using System;

namespace ConcerningConcerns
{
	public class ClassB: TestsBase
	{
		public void test()
		{
			report("test");
			test2();
		}

		public void test2()
		{
			report("test2");
		}
	}
}
