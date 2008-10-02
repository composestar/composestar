
concern wrong06_duplicate
{
	filtermodule FM1
	{
	}
	
	filtermodule FM2
	{
	}
	
	filtermodule FM2
	{
		internals
			foo: test;
	}
	
	filtermodule FM3
	{
		internals
			foo: test;
			foo: test;
			foo2: test;
		externals
			foo2: test;
			foo3: test;
		conditions
			foo3: foo.bar;
			foo4: foo.bar;
	}
	
	filtermodule FM4
	{
		inputfilters
			f1 : Dispatch = (true);
			f1 : Dispatch = (true);
			f2 : Dispatch = (true)
		outputfilters
			f2 : Send = (true);
			f3 : Send = (true);
			f3 : Send = (true)
	}
}
