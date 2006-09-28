concern FilterTestsConcern in BasicTests
{

	filtermodule FM2
	{
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