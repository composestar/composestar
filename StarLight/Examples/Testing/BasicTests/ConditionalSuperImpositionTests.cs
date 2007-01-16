using System;
using System.Collections.Generic;
using System.Text;

namespace BasicTests
{
	public class ConditionalSuperImpositionTests : TestsBase
	{
		public static Boolean isEnabled;

		public ConditionalSuperImpositionTests()
		{
		}

		public void CondSIFunc1()
		{
			report("Executing CondSIFunc1...");
		}

		public static Boolean CheckEnabled()
		{
			return isEnabled;
		}
	}
}
