concern ConditionalSuperImpositionTestsConcern in BasicTests
{
	filtermodule FM3
	{
		inputfilters
			condDisp : Dispatch = { True => [*.CheckEnabled] };
			logging  : Logging = { True => [*.*] }
	}

	superimposition
	{
		conditions
		    checkEnabled : BasicTests.ConditionalSuperImpositionTests.CheckEnabled;
		selectors
			csiClass = { C | isClassWithName(C, 'BasicTests.ConditionalSuperImpositionTests') };
		filtermodules
			checkEnabled => csiClass <- FM3;
	}
}