using System;

namespace BasicTests
{
	public class TestExternal : TestsBase
	{
		private static TestExternal instance = null;
		
		private TestExternal()
		{
		}
		
		public static TestExternal getInstance()
		{
			if (instance == null) instance = new TestExternal();
			
			return instance;
		}

		public void externalMe()
		{
			report("internalMe");
		}

	}
}
