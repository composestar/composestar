concern BulkUpdates in InventoryTwo
{
	filtermodule BulkUpdate
	{
		internals
			bulk: InventoryTwo.BulkUpdater;
		inputfilters
			addproducts:    Dispatch = { [*.AddProducts] bulk.AddProducts };
			removeproducts: Dispatch = { [*.RemoveProducts] bulk.RemoveProducts }
	}

	superimposition
	{
		selectors
			subjects = { C | isClassWithName(C, 'InventoryTwo.Inventory') };
		filtermodules
			subjects <- BulkUpdate;
	}
}
