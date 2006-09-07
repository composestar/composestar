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
			selA <- logging::log( FilterModuleParameter.Logger );
			selA <- genericInheritance( FilterModuleParameter.Animal );
		constraints
			pre (genericInheritance, logger::log);
	}
}