concern Platypus in PlatypusExample
{
	filtermodule MultitpleInheritance
	{
		internals
			m : PlatypusExample.Mammal;
			b : PlatypusExample.Bird;
		outputfilters
			eat : Dispatch = { [*.eat] m.eat };
			hungry : Dispatch = { [*.isHungry] m.isHungry }
	}
	superimposition
	{
		selectors
			files = { C | isClassWithName(C, 'PlatypusExample.Platypus') };
		filtermodules
			files <- MultitpleInheritance;
	}
}