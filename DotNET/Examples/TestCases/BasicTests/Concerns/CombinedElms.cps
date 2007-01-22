concern CombinedElms in BasicTests
{
	filtermodule multiPatterns
	{
		inputfilters
			mpat1 : Dispatch = { True ~> {[*.foo], [*.bar], [*.ignored], [*.FooBarQuux]} inner.success };
			mpat2 : Dispatch = { 
									[*.foo] *.bar, 
									[*.bar] *.quux 
								}
	}

	superimposition
	{
		selectors
			foobar = { C | isClassWithName(C, 'BasicTests.FooBarQuux')};
		filtermodules
			foobar <- multiPatterns;
	}
}
