concern FilterTestsConcern in BasicTests
{
	filtermodule FM1
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
			before2		: Before = { True => [*.func7] myExternal.before, True => [*.func9] myExternal.before, True => [*.func10] myExternal.before };
			after1		: After =  { True => [*.func4] myExternal.after };
			after2		: After =  { True => [*.func9] myExternal.after, True => [*.func10] myExternal.after };
			dispatch1	: Dispatch = { True => [*.func1] myExternal.externalMe };
			dispatch2	: Dispatch = { True => [*.func2] myInternal.internalMe };
			dispatch3	: Dispatch = { True => [*.func3] inner.func1 }
		outputfilters
			aftertest2  : After = { True => [*.func4] myExternal.after2 }
	}
	
	filtermodule FM2
	{
		inputfilters
			logging     : Logging = { True => [*.*] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
			baseStaticClass = { C | isClassWithName(C, 'BasicTests.StaticFilterTests`1') };
			valuetypeClass = { C | isClassWithName(C, 'BasicTests.MyValueType') };
			//{ C | isClass(C), isClassWithName(C, P), matchPattern(P, 'BasicTests.StaticFilterTests.*') };
 		filtermodules
			baseClass <- FM1;
			baseStaticClass <- FM2;
			valuetypeClass <- FM2;
	}
}