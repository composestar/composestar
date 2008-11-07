#region Using directives
using System;
using System.Collections.Generic;
using System.Text;
#endregion

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

		//[Composestar.StarLight.SkipWeaving]
		[Composestar.StarLight.Weaving.ConditionParameters.ConditionParameterEmpty]
		public static Boolean LoggingEnabled()
		{
			return loggingEnabled;
		}

		//[Composestar.StarLight.SkipWeaving]
		[Composestar.StarLight.Weaving.ConditionParameters.ConditionParameterMethodInfo]
		public static Boolean TimingEnabled(System.Reflection.MethodBase methodInfo)
		{
			return timingEnabled && methodInfo.Name.Equals("Test");
		}

		//[Composestar.StarLight.SkipWeaving]
		[Composestar.StarLight.Weaving.ConditionParameters.ConditionParameterFQN]
		public static Boolean ShouldLog(String fqn)
		{
			Console.WriteLine("Should log function '{0}' ? {1}", fqn, loggingEnabled.ToString());
			return loggingEnabled;
		}

		//[Composestar.StarLight.SkipWeaving]
		[Composestar.StarLight.Weaving.ConditionParameters.ConditionParameterNS]
		public static Boolean ShouldLogExt(String ns, String type)
		{
			Console.WriteLine("Should log ns '{0}', type '{1}' ? {2}", ns, type, loggingEnabled.ToString());
			return loggingEnabled;
		}
	}
}
