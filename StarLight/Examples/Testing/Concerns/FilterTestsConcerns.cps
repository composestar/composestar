concern FilterTestsConcern in BasicTests
{

	filtermodule FM2
	{
	  internals
	    myInternal : BasicTests.TestInternal;
		conditions
			doError : inner.getProduceError();
		inputfilters
			makeError : Error = {  doError ~> [*.makeError], !doError => [*.*] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'BasicTests.FilterTests') };
		filtermodules
			baseClass <- FM2;
	}
}