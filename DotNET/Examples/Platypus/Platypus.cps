concern PlatypusConcern in PlatypusExample
{
	filtermodule MultipleInheritance
	{
		internals
			m : PlatypusExample.Mammal;
			b : PlatypusExample.Bird;
		inputfilters
			rest : Dispatch = { <inner.*> inner.*, <m.*> m.*, <b.*> b.* }
	}
	superimposition
	{
		selectors
			platypus = { C | isClassWithName(C, 'PlatypusExample.Platypus') };
		filtermodules
			platypus <- MultipleInheritance;
	}
}