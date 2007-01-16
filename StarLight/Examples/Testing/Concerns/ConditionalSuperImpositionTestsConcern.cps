concern ConditionalSuperImpositionTestsConcern in BasicTests
{
	filtermodule FM3
	{
		inputfilters
			logging  : Logging = { True => [*.*] }
	}
	
	filtermodule FM4
	{
		inputfilters
			timing : Timing = { True => [*.*] }
	}

	superimposition
	{
		conditions
		    loggingEnabled : BasicTests.ConditionalSuperImpositionTests.LoggingEnabled;
		    timingEnabled : BasicTests.ConditionalSuperImpositionTests.TimingEnabled;
		selectors
			csiClass = { C | isClassWithName(C, 'BasicTests.ConditionalSuperImpositionTests') };
		filtermodules
			loggingEnabled => csiClass <- FM3;
			timingEnabled => csiClass <- FM4;
	}
}