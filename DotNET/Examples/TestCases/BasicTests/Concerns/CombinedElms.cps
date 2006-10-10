concern CombinedElms in BasicTests
{
	filtermodule multiPatterns
	{
		inputfilters
			mpat : Dispatch = { True => { [*.foo] *.bar, [*.bar] *.quux, [*.quux] inner.success } }
	}

	superimposition
	{
		selectors
			foobar = { C | isClassWithName(C, 'BasicTests.FooBarQuux')};
		filtermodules
			foobar <- multiPatterns;
	}
}