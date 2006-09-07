concern BulkUpdates in ExampleAOP {
	filtermodule BulkUpdate {
		internals
			bulk: ExampleAOP.BulkUpdater;
		inputfilters
			addproducts: Dispatch = {[*.AddProducts] bulk.AddProducts};
			removeproducts: Dispatch = {[*.RemoveProducts] bulk.RemoveProducts}
	}

	superimposition {
		selectors
			subjects = {C | isClassWithName(C, 'ExampleAOP.Inventory')};
		filtermodules
			subjects <- BulkUpdate;
	}
}
