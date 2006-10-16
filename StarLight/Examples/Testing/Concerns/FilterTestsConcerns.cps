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
		    //produceerrordisp : Dispatch = { True => [*.getProduceError] };
		    logging : Logging = { True => [*.*] };
		    profiling: Profiling = { True => [*.*] };
			makeError : Error = {  doError ~> [*.makeError], !doError => [*.*] };
			aftertest : After = { True => [*.func4] myExternal.after };
			test : Dispatch = { True => [*.func1] myExternal.externalMe };
			test2 : Dispatch = { True => [*.func2] myInternal.internalMe };
			test3 : Dispatch = { True => [*.func3] inner.func1 }
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