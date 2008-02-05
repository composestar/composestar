concern mainprint
{
	filtermodule noVersionHeader
	{
		inputfilters
			noversion : Dispatch = { [*.printHeaderWithVersion] *.printPlainHeader }
	}
	
	filtermodule optionalProgramName
	{
		conditions
			printit : inner.doPrintProgramName();
		inputfilters
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
