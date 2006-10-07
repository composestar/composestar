concern inheritance in FilterModuleParameter{
	filtermodule genericInheritance(?parent){
		internals
			parent : ?parent;
		inputfilters
			d : Dispatch ={<inner.*> inner.*, <parent.*> parent.*, [*.*] *.*}
	}

	superimposition{
		selectors
			selA = {C | isClassWithNameInList(C, 
             ['FilterModuleParameter.Cat',
							'FilterModuleParameter.Iguanidae'])};
		filtermodules
			selA <- genericInheritance( FilterModuleParameter.Animal );
			selA <- logging::log( FilterModuleParameter.Logger, {walk, makeNoise});
		constraints
			pre (genericInheritance, logger::log);
	}
}