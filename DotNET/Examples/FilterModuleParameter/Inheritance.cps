concern inheritance in FilterModuleParameter
{
	filtermodule genericInheritance(?parent)
	{
		internals
			parent : ?parent;
		inputfilters
			d : Dispatch = { <inner.*> inner.*, <parent.*> parent.* }
	}

	superimposition
	{
		selectors
			Pet = {C | isClassWithNameInList(C, 
				['FilterModuleParameter.Cat', 'FilterModuleParameter.Iguanidae'])};
		filtermodules
			Pet <- genericInheritance( FilterModuleParameter.Animal );
			Pet <- logging::log( FilterModuleParameter.Logger, {walk, makeNoise});
		constraints
			pre (genericInheritance, logger::log);
	}
}