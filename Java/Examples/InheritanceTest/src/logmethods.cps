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
	
	superimposition
	{
		selectors
			childs = { C | isClassWithName(C, 'Child') };
		filtermodules
			childs <- logall;
	}
}