concern TypeTestsConcern in BasicTests
{
	filtermodule RewriteDummies
	{
		inputfilters
			toReal: Dispatch = 
			{
				[*.dummyBool] *.realBool,
				[*.dummyByte] *.realByte,
				[*.dummyInt] *.realInt,
				[*.dummyFloat] *.realFloat,
				[*.dummyDouble] *.realDouble,
				[*.dummyString] *.realString,
				[*.dummyUInt64] *.realUInt64,
				[*.dummyInterface] *.realInterface
				
			}
	}

	superimposition
	{
		selectors
			typeTests = { C | isClassWithName(C, 'BasicTests.TypeTests') };
		filtermodules
			typeTests <- RewriteDummies;
	}
}