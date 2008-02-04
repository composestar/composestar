concern calcfix
{
	filtermodule correctDiv
	{
		inputfilters
			divismult : Dispatch = { [*.div] *.mult }
	}
	
	superimposition
	{
		selectors
			calc = { C | isClassWithName(C, 'calc') };
		filtermodules
			calc <- correctDiv;
	}
}
