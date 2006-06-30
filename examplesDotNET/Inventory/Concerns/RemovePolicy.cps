concern RemovePolicy in ExampleAOP {
	filtermodule RemovePolicyModule {
		internals
			policy: ExampleAOP.RemovePolicy;
		conditions
			disabled: policy.RemoveDisabled();
		inputfilters
			enable: Dispatch = {[*.EnableRemove] policy.EnableRemove };
			disable: Dispatch = {[*.DisableRemove] policy.DisableRemove };
			error: Error = {disabled ~> [*.RemoveProducts]}
	}

	superimposition {
		selectors
			inventory = {C | isClassWithName(C, 'ExampleAOP.Inventory')};
		filtermodules
			inventory <- RemovePolicyModule;
	}
}
