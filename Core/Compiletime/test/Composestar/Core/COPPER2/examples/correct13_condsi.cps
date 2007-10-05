// Condition super imposition
concern ConditionalSuperImpositionTestsConcern in BasicTests
{
	filtermodule FM3
	{}
	
	filtermodule FM4
	{}
	
	superimposition
	{
		conditions
      loggingEnabled : BasicTests.ConditionalSuperImpositionTests.LoggingEnabled;
		  timingEnabled : BasicTests.ConditionalSuperImpositionTests.TimingEnabled;
		selectors
			csiClass = { C | isClassWithName(C, 'BasicTests.ConditionalSuperImpositionTests') };
		filtermodules
		  csiClass <- FM3;
			loggingEnabled => csiClass <- FM3, FM4;
			timingEnabled => csiClass <- FM4;
	}
}
