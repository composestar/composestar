concern ConditionConcern in Conditions
{
	filtermodule WithConditions
	{
		internals
			conhelper : ConditionTests.ConditionHelper;
		conditions
			cond1 : conhelper.cond1(); // false
			cond2 : conhelper.cond2(); // true
			cond3 : conhelper.cond3(); // true
		inputfilters
			f1 : Dispatch = { False | True => [*.func1] inner.func1correct }; // true
			f2 : Dispatch = { False => [*.func2] inner.* }; // false
			f2a : Dispatch = { (False & True) => [*.func2] inner.* }; // false
			f3 : Dispatch = { cond1 => [*.func2] inner.* }; // false
			f4 : Dispatch = { (cond1 & cond2) => [*.func2] inner.* }; // false
			f5 : Dispatch = { (cond1 | cond2) => [*.func2] inner.func2correct }; // true
			f5a : Dispatch = { !True => [*.func3] inner.* }; // false
			f6 : Dispatch = { (!cond1 & cond2) => [*.func3] inner.func3correct }; // true
			f7 : Dispatch = { ((!cond1 & cond2) | cond3) => [*.func4] inner.func4correct }; // true
			f8 : Dispatch = { !(cond1 & cond2) => [*.func5] inner.func5correct } // false
	}

	superimposition
	{
		selectors
			typeTests = { C | isClassWithName(C, 'ConditionTests.ConditionClass') };
		filtermodules
			typeTests <- WithConditions;
	}
}
