concern logmethods
{
	filtermodule logall
	{
		internals
			logr : Logger;
		inputfilters
			logIn : Before(target = logr, selector = 'logIn') = (true)
		outputfilters
			logOut : Before(target = logr, selector = 'logOut') = (!(selector == 'println'))
	}
	
	// A dummy filter module used to fix this program
	filtermodule dummy 
	{
		inputfilters
			d1 : Dispatch = (false)
		outputfilters
			d2 : Send = (false)
	}
	
	superimposition
	{
		selectors
			childs = { C | isClassWithName(C, 'Child') };
			//bases = { C | isClassWithName(C, 'Base') };
		filtermodules
			childs <- logall;
			//bases <- dummy; // this binding will "fix" the broken behavior and make the output of this test correct
	}
}