concern BulkUpdates in InventoryTwo
{
	filtermodule BulkUpdate
	{
		internals
			bulk: InventoryTwo.BulkUpdater;
		inputfilters
			add: Dispatch = { [*.AddProducts] bulk.AddProducts };
			rem: Dispatch = { [*.RemoveProducts] bulk.RemoveProducts }
	}

	superimposition
	{
		selectors
			subjects = { C | isClassWithName(C, 'InventoryTwo.Inventory') };
		filtermodules
			subjects <- BulkUpdate;
	}
}
