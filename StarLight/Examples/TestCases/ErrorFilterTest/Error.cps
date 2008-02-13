concern ErrorTestConcern
{
	filtermodule ErrorTest
	{
		inputfilters
			// disable calls to method Test2
			test_error : Error = { True ~> [*.f2] }
	}
	superimposition
	{
		selectors
			S = { C | isClassWithName(C, 'ErrorFilterTest.Subject') };
			
		filtermodules
			S <- ErrorTest;
	}
}