concern offoobar
{
	filtermodule offm
	{
		outputfilters
			tobar : Send = (selector == 'foo') { selector = 'bar'; } 
	}
	
	superimposition
	{
		selectors
			main = { C | isClassWithName(C, 'main') };
		filtermodules
			main <- offm;
	}
}
