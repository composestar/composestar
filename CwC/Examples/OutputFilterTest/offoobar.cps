concern offoobar
{
	filtermodule offm
	{
		outputfilters
			tobar : Send = { [*.foo] *.bar } 
	}
	
	superimposition
	{
		selectors
			main = { C | isClassWithName(C, 'main') };
		filtermodules
			main <- offm;
	}
}
