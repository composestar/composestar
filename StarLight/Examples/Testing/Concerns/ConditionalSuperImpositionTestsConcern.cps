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

    filtermodule FM5
	{
		inputfilters
			logger : Logging = { True => [*.*] }
	}
	
	filtermodule FM6
	{
		inputfilters
			loggerExt : Logging = { True => [*.*] }
	}
	
	superimposition
	{
		conditions
		    loggingEnabled : BasicTests.ConditionalSuperImpositionTests.LoggingEnabled;
		    timingEnabled : BasicTests.ConditionalSuperImpositionTests.TimingEnabled;
		    shouldLog : BasicTests.ConditionalSuperImpositionTests.ShouldLog;
		    shouldLogExt : BasicTests.ConditionalSuperImpositionTests.ShouldLogExt;
		selectors
			csiClass = { C | isClassWithName(C, 'BasicTests.ConditionalSuperImpositionTests') };
		filtermodules
			loggingEnabled => csiClass <- FM3;
			timingEnabled => csiClass <- FM4;
			shouldLog => csiClass <- FM5;
			shouldLogExt => csiClass <- FM6;
		constraints
			pre(FM5,FM3);
			pre(FM5,FM6);
			pre(FM6,FM3);
			pre(FM3,FM4);
	}
}