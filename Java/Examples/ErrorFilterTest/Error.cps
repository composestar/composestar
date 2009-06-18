concern ErrorTestConcern
{
	filtermodule ErrorTest
	{
		inputfilters
			// The error filter throws an exception when the message is rejected
			// Reject the message when the selector is f2
			test_error : Error(message='Compose* error filter exception!') = ( !(selector == 'f2') )
	}
	
	superimposition
	{
		selectors
			S = { C | isClassWithName(C, 'ErrorFilterTest.Subject') };			
		filtermodules
			S <- ErrorTest;
	}
}
