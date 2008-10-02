// various filter constructions
concern correct16_filterargs in ConcernExamples
{
	filtermodule simple
	{
		inputfilters
			f1 : Before(target=inner, selector='foo') = (true);
			f2 : Meta(foo="bar") = (true);
			f3 : Dispatch() = (true)
	}
}
