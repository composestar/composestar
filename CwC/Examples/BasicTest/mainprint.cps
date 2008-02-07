concern mainprint
{
	filtermodule noVersionHeader
	{
		inputfilters
			befr : Before = { [*.printHeaderWithVersion] inner.beforeSomeThing };
			aftr : After = { [*.printHeaderWithVersion] *.afterSomeThing };
			noversion : Dispatch = { [*.printHeaderWithVersion] *.printPlainHeader }
	}
	
	filtermodule optionalProgramName
	{
		conditions
			printit : inner.doPrintProgramName();
		inputfilters			
			befr : Before = { [*.printProgramName] *.beforeSomeThing };
			aftr : After = { [*.printProgramName] *.afterSomeThing };
			maybeprint : Dispatch = { !printit => [*.printProgramName] *.printNoName }
	}	
	
	superimposition
	{
		selectors
			main = { C | isClassWithName(C, 'main') };
		filtermodules
			main <- noVersionHeader;
			main <- optionalProgramName;
	}
}
