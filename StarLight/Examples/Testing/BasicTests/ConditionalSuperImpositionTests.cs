using System;
using System.Collections.Generic;
using System.Text;

namespace BasicTests
{
	public class ConditionalSuperImpositionTests : TestsBase
	{
		public static Boolean loggingEnabled;
		public static Boolean timingEnabled;

		public ConditionalSuperImpositionTests()
		{
		}

		public void CondSIFunc1()
		{
			report("Executing CondSIFunc1...");
		}

		[Composestar.StarLight.SkipWeaving()]
		public static Boolean LoggingEnabled()
		{
			return loggingEnabled;
		}

		[Composestar.StarLight.SkipWeaving()]
		public static Boolean TimingEnabled()
		{
			return timingEnabled;
		}
	}
}
