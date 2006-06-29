concern BulkAddProducts in ExampleAOP {
	filtermodule BulkAdd {
		internals
			bulk: ExampleAOP.BulkAdder;
		inputfilters
			bulkadd: Dispatch = {[*.AddProducts] bulk.AddProducts}
	}

	superimposition {
		selectors
			subjects = {C | isClassWithName(C, 'ExampleAOP.Inventory')};
		filtermodules
			subjects <- BulkAdd;
	}
}
