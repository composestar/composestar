concern InventoryObserver in ExampleAOP {
	filtermodule SubjectInventoryFilter {
		internals
			subject: ExampleAOP.Subject;
		conditions
			observed: subject.Observed();
		inputfilters
			attach: Dispatch = {[*.Attach] subject.Attach};
			detach: Dispatch = {[*.Detach] subject.Detach};
			change: Meta = {observed => [*.AddProduct] subject.Notify,
			                observed => [*.RemoveProduct] subject.Notify,
			                observed => [*.AddProducts] subject.Notify,
			                observed => [*.RemoveProducts] subject.Notify}
	}

	/*
	 * Note that the only difference between these two observer filters
	 * is the internal object and the constructor of register filter.
	 * Parameterized filtermodules would be grand!
	 */
	filtermodule ObserverInventoryDisplayFilter {
		internals
			observer: ExampleAOP.ObserverInventoryDisplay;
		inputfilters
			register: Meta = {[*.InventoryDisplay] observer.Register};
			update: Dispatch = {[*.Update] observer.Update}
	}

	filtermodule ObserverInventoryCountFilter {
		internals
			observer: ExampleAOP.ObserverInventoryCount;
		inputfilters
			register: Meta = {[*.InventoryCount] observer.Register};
			update: Dispatch = {[*.Update] observer.Update}
	}

	superimposition {
		selectors
			subjects = {C | isClassWithName(C, 'ExampleAOP.Inventory')};
			observerids = {C | isClassWithName(C, 'ExampleAOP.InventoryDisplay')};
			observerics = {C | isClassWithName(C, 'ExampleAOP.InventoryCount')};
		filtermodules
			subjects <- SubjectInventoryFilter;
			observerids <- ObserverInventoryDisplayFilter;
			observerics <- ObserverInventoryCountFilter;
	}
}
