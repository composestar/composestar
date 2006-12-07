concern InventoryObserver in InventoryTwo
{
	filtermodule SubjectInventoryFilter
	{
		internals
			subject: InventoryTwo.Subject;
		inputfilters
			ad:  Dispatch = { [*.Attach] subject.Attach, 
			                  [*.Detach] subject.Detach };
			notify: After = { [*.AddProduct] subject.Notify,
			                  [*.RemoveProduct] subject.Notify }
	}

	superimposition
	{
		selectors
			subjects = { C | isClassWithName(C, 'InventoryTwo.Inventory') };
		filtermodules
			subjects <- SubjectInventoryFilter;
	}
}
