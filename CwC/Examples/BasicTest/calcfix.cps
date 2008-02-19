concern calcfix
{
	filtermodule correctDiv
	{
		inputfilters
			divismult : Dispatch = { [*.divide] *.mult };
			sffds : Dispatch = { [*.multiply] *.mult }
	}
	
	superimposition
	{
		selectors
			calc = { C | isClassWithName(C, 'calc') };
		filtermodules
			calc <- correctDiv;
	}
}
