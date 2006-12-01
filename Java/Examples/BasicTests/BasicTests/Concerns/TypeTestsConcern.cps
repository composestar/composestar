concern TypeTestsConcern in BasicTests
{
	filtermodule RewriteDummies
	{
		inputfilters
			toReal: Dispatch = 
			{
				[*.dummyBool] *.realBool,
				[*.dummyByte] *.realByte,
				[*.dummyChar] *.realChar,
				[*.dummyInt] *.realInt,
				[*.dummyFloat] *.realFloat,
				[*.dummyDouble] *.realDouble,
				[*.dummyString] *.realString,
				[*.dummyShort] *.realShort,
				[*.dummyLong] *.realLong,
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