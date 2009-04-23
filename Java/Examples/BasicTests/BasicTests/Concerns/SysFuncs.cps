concern SysFuncTestOne in BasicTests
{
	filtermodule toStringRedir
	{
		internals
			sftt : BasicTests.SysFuncTestTwo;
		inputfilters
			tsr : Dispatch = (selector == 'toString') { target = sftt; }
	}
	
	superimposition
	{
		selectors
			slf = { C | isClassWithName(C, 'BasicTests.SysFuncTestOne') };
		filtermodules
			slf <- toStringRedir;
	}
}
