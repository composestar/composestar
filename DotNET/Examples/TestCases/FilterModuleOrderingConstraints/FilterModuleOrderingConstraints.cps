concern FilterModuleOrderingConstraints
{
	filtermodule FM1
	{
		internals
			fp : FilterModuleOrderingConstraints.FilterPrinter;
		inputfilters
			printer : Meta = { [*.sayHello]fp.printFM1 }
	}
	
	filtermodule FM2
	{
		internals
			fp : FilterModuleOrderingConstraints.FilterPrinter;
		inputfilters
			printer : Meta = { [*.sayHello]fp.printFM2 }
	}

	filtermodule FM3
	{
		internals
			fp : FilterModuleOrderingConstraints.FilterPrinter;
		inputfilters
			printer : Meta = { [*.sayHello]fp.printFM3 }
	}

	filtermodule FM4
	{
		internals
			fp : FilterModuleOrderingConstraints.FilterPrinter;
		inputfilters
			printer : Meta = { [*.sayHello]fp.printFM4 }
	}
	
	superimposition
	{
		selectors
			foo = { Class | isClassWithName(Class,'FilterModuleOrderingConstraints.Foo') };
		filtermodules
			foo <- FM1,FM2,FM3,FM4;
		constraints // Should be: FM1 FM3 FM4 FM2
			pre(FM1,FM2);
			pre(FM1,FM3);
			pre(FM1,FM4);
			pre(FM3,FM4);
			pre(FM4,FM2);
	}
}
