concern mainprint
{
	filtermodule noVersionHeader
	{
		inputfilters
			divismult : Dispatch = { [*.printHeaderWithVersion] *.printPlainHeader }
	}
	
	filtermodule optionalProgramName
	{
		conditions
			printit = inner.doPrintProgramName();
		inputfilters
			maybeprint : Dispatch = { !printit => [*.printProgramName] *.printVoid }
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
