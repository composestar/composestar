// test case for filter module parameters
concern inheritance in FilterModuleParameter
{
	filtermodule genericInheritance(?parent)
	{
		internals
			parent : ?parent;
		inputfilters
			d : Dispatch = { <inner.*> inner.*, <parent.*> parent.* }
	}
	
	filtermodule log(?logger, ??walkfunction)
	{
		internals
			logger : ?logger;
		inputfilters
			m : Meta = { [*.??walkfunction] logger.log }
	}

	superimposition
	{
		selectors
			Pet = {C | isClassWithNameInList(C, 
				['FilterModuleParameter.Cat', 'FilterModuleParameter.Iguanidae'])};
		filtermodules
			Pet <- genericInheritance( FilterModuleParameter.Animal );
			Pet <- log( FilterModuleParameter.Logger, {walk, makeNoise});
	}
}
