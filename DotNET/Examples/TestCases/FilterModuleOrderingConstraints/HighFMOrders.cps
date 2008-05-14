concern HighFMOrders
{
	filtermodule FM1
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM2
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM3
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM4
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM5
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM6
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM7
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM8
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}

	filtermodule FM9
	{
		inputfilters
			f1 : Meta = { [*.foo] inner.bar }
	}
	
	superimposition
	{
		selectors
			dummy = { Class | isClassWithName(Class,'FilterModuleOrderingConstraints.Dummy') };
		filtermodules
			dummy <- FM1,FM2,FM3,FM4,FM5,FM6,FM7,FM8,FM9;
	}
}
