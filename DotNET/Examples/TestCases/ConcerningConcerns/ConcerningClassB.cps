concern ConcerningClassC
{
	filtermodule FM1
	{
		internals
			cc : ConcerningConcerns.ClassC;
		inputfilters
			toc : Meta = { [*.test2] cc.test }
	}

	superimposition
	{
		selectors
			cb = { C | isClassWithName(C, 'ConcerningConcerns.ClassB') };
		filtermodules
			cb <- FM1;
	}
}