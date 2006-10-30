concern FilterTestsConcern in BasicTests
{
	filtermodule FM1
	{
		internals
			helper : BasicTests.FilterTestsHelper;
		inputfilters
			toNext1 : Dispatch = { [*.func1] *.func2 };
			doStuff : Meta = { [*.doStuff] helper.doStuffAround }
		outputfilters
			toHelper : Send = { [*.helpMe] helper.helped }
	}

	filtermodule FM2
	{
		conditions
			doError : inner.getProduceError();
		inputfilters
			makeError : Error = { doError ~> [*.setProduceError], !doError => [*.*] };
			toNext2 : Dispatch = { [*.func2] *.func3 }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
		filtermodules
			baseClass <- FM1;
			baseClass <- FM2;
	}
}