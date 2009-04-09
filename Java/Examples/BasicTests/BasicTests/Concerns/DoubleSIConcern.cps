concern DoubleSIConcern
{
	filtermodule helpfm
	{
		internals
			h : BasicTests.DoubleFMSIHelper;
		inputfilters
			th : Before(target = h, selector = 'before') = ( selector == 'run' )
	}
	
	superimposition
	{
		selectors
			s = { C | isClassWithName(C, 'BasicTests.DoubleFMSI') };
		filtermodules
			s <- helpfm, helpfm, helpfm;
	}
}