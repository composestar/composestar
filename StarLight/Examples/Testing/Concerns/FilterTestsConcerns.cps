concern FilterTestsConcern in BasicTests
{
	filtermodule FM2
	{
		internals
			myInternal : BasicTests.TestInternal;
			myInteger : System.Int32;
			myBigInteger : System.Int64;
			myString : System.String;
		externals
			myExternal : BasicTests.TestExternal = BasicTests.TestExternal.getInstance();
		conditions
			doError : inner.getProduceError();
		inputfilters
			logging		: Logging = { True => [*.*] };
			profiling	: Profiling = { True => [*.*] };
			error1		: NotImplemented = { doError ~> [*.makeError], !doError => [*.*] };
			//error2	: Dispatch = { True => [*.getProduceError] };
			//error3	: Error = { doError ~> [*.makeError], !doError => [*.*] };
			before1	: Before = { True => [*.func4] myExternal.before };
			before2		: Before = { True => [*.func7] myExternal.before };
			after1		: After =  { True => [*.func4] myExternal.after };
			dispatch1	: Dispatch = { True => [*.func1] myExternal.externalMe };
			dispatch2	: Dispatch = { True => [*.func2] myInternal.internalMe };
			dispatch3	: Dispatch = { True => [*.func3] inner.func1 }
		outputfilters
			aftertest2 : After = { True => [*.func4] myExternal.after2 }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
		filtermodules
			baseClass <- FM2;
	}
}