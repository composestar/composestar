concern PlatypusConcern in PlatypusExample
{
	filtermodule MultipleInheritance
	{
		internals
			m : PlatypusExample.Mammal;
			b : PlatypusExample.Bird;
		inputfilters
			eat : Dispatch = { [*.eat] m.eat };
			hungry : Dispatch = { [*.isHungry] m.isHungry }
	}
	superimposition
	{
		selectors
			platypus = { C | isClassWithName(C, 'PlatypusExample.Platypus') };
		filtermodules
			platypus <- MultipleInheritance;
	}
}