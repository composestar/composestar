concern FilterTestsConcern in BasicTests
{

	filtermodule FM2
	{
	  internals
	    myInternal : BasicTests.TestInternal;
	  externals
	  	myExternal : BasicTests.TestExternal = BasicTests.TestExternal.getInstance();

		conditions
			doError : inner.getProduceError();
		inputfilters
		    produceerrordisp : Dispatch = { True => [*.getProduceError] };
			makeError : Error = {  doError ~> [*.makeError], !doError => [*.*] };
			beforetest : After = { True => [*.func4] myExternal.after };
			test : Dispatch = { True => [*.func1] myExternal.externalMe };
			test2 : Dispatch = { True => [*.func2] myInternal.internalMe };
			test3 : Dispatch = { True => [*.func3] inner.func1 }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
		filtermodules
			baseClass <- FM2;
	}
}