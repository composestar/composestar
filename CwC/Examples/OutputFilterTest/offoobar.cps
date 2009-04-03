concern offoobar
{
	filtermodule offm
	{
		outputfilters
			report : Before(selector = 'reportCall') = (selector == 'foo');
			tobar : Send = (selector == 'foo') { selector = 'bar'; };
			other : Send = (selector == 'otherT1') { selector = 't1'; }  
	}
	
	superimposition
	{
		selectors
			main = { C | isClassWithName(C, 'main') };
		filtermodules
			main <- offm;
	}
}
