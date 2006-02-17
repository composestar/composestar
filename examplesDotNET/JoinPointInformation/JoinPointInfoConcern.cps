concern JoinPointInfoConcern(ext_foo : JPInfo.Foo ; ext_bar : JPInfo.Bar) in JPInfo
{
	filtermodule JPInfoModule
	{
		internals
			in_foo : JPInfo.Foo;
			in_bar : JPInfo.Bar;
			jpie   : JPInfo.JoinPointInfoExtractor;
		conditions
			cond : jpie.doCondition();
		inputfilters
			meta : Meta = { cond => [*.*] jpie.extract }
	}

	superimposition
	{
		selectors
			selectees = { C | isClassWithName(C, 'JPInfo.JoinPointInfoConcern') };
		filtermodules
			selectees <- JPInfoModule;
	}

	implementation by JPInfo.JoinPointInfoConcern;
}