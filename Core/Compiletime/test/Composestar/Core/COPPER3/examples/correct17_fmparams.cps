// test case for filter module parameters
concern correct17_fmparams in Concern.Examples
{
	filtermodule genericInheritance(?parent)
	{
		internals
			parent : ?parent;
		inputfilters
			d : Dispatch = ( selector $= parent | selector $= ?parent )
				{
					message.parentType = ?parent;
				}
	}
	
	filtermodule log(?logger, ??walkfunction)
	{
		internals
			logger : ?logger;
		inputfilters
			m : Meta = ( selector == ??walkfunction )
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
