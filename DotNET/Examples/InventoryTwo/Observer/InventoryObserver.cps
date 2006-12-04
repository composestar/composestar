concern InventoryObserver in InventoryTwo
{
	filtermodule SubjectInventoryFilter(??m)
	{
		internals
			subject: InventoryTwo.Subject;
		inputfilters
			ad: Dispatch = { [*.Attach] subject.Attach, 
			                 [*.Detach] subject.Detach };
			notify: Meta = { [*.??m]    subject.Notify }
	//		notify: Meta = { @Observed  subject.Notify }
	}

	superimposition
	{
		selectors
			subjects = { C | isClassWithName(C, 'InventoryTwo.Inventory') };

	//		observed = { M | isClassWithName(C, 'InventoryTwo.Inventory')
	//		               , classHasMethod(C, M), methodHasAnnotationWithName(M, 'InventoryTwo.Observed') };
		filtermodules
			subjects <- SubjectInventoryFilter({AddProduct, RemoveProduct});
	//		subjects <- SubjectInventoryFilter(observed);
	}
}
