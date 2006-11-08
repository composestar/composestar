concern ConcerningClassA
{
	filtermodule FM1
	{
		internals
			cb : ConcerningConcerns.ClassB;
		inputfilters
			tob : Dispatch = { [*.test] cb.test }
	}

	superimposition
	{
		selectors
			ca = { C | isClassWithName(C, 'ConcerningConcerns.ClassA') };
		filtermodules
			ca <- FM1;
	}
}