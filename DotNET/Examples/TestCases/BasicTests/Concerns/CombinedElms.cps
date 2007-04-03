/*
The goal of this concern is to test the matching pattern list and disable operator
*/
concern CombinedElms in BasicTests
{
	filtermodule multiPatterns
	{
		internals
			base: BasicTests.TestsBase;
		inputfilters
			// ignore constructor and base methods
			ign : Dispatch = { {<base.*>, [*.FooBarQuux]} inner.* };
			// don't only allow these methods, report is actually allowed because of the previous filter
			mpat1 : Error = { True ~> {[*.quux], [*.NonExistant], [*.report]} };
			mpat2 : Dispatch = { 
									[*.foo] inner.bar, 
									[*.bar] inner.quux 
								};
			bogus : Dispatch = { {[*.quux2], [*.quux3]} inner.quux }
	}

	superimposition
	{
		selectors
			foobar = { C | isClassWithName(C, 'BasicTests.FooBarQuux')};
		filtermodules
			foobar <- multiPatterns;
	}
}
