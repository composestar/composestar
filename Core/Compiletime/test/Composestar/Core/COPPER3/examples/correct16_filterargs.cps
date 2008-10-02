// various filter constructions
concern correct16_filterargs in ConcernExamples
{
	filtermodule simple
	{
		inputfilters
			f1 : Before(target=inner, selector='foo') = (true) {
				filter.target = inner;
				filter.selector = 'foo';
			};
			f2 : Meta(foo="bar") = (true) { filter.foo = "bar"; };
			f3 : Dispatch() = (true)
	}
}
