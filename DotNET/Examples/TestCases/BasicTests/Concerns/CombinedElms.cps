concern CombinedElms in BasicTests
{
	filtermodule multiPatterns
	{
		/*
		inputfilters
			// this is work in progress, it doesn't work yet, but it should in the future
			
			mpat1 : Dispatch { True & True ~> {[*.foo], [*.success]} inner.success} }
			mpat2 : Dispatch { [*.foo] *.bar, [*.bar] *.quux }
		*/
	}

	superimposition
	{
		selectors
			foobar = { C | isClassWithName(C, 'BasicTests.FooBarQuux')};
		filtermodules
			foobar <- multiPatterns;
	}
}