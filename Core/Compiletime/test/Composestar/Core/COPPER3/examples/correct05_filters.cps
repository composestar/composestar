// various filter constructions
concern correct05_filters in ConcernExamples
{
	filtermodule simple
	{
		inputfilters
			f1 : Dispatch = { [*.foo] *.bar };
			f2 : Dispatch = { <inner.*> inner.* };
			f3 : Dispatch = { [*.foo] };
			f4 : Dispatch = { <inner.*> }
	}
	
	filtermodule basicconds
	{
		conditions
			cond : inner.cond();
		inputfilters
			f1 : Dispatch = { True => [*.foo] };
			f2 : Dispatch = { False => [*.foo] };
			f3 : Dispatch = { cond => [*.foo] };
			f4 : Dispatch = { True ~> [*.foo] }
	}
	
	filtermodule condexpr
	{
		conditions
			cond1 : inner.cond();
			cond2 : inner.cond();
			cond3 : inner.cond();
		inputfilters
			f0 : Dispatch = { (cond1) => [*.foo] };
			f1 : Dispatch = { !cond1 => [*.foo] };
			f2 : Dispatch = { !(cond1) => [*.foo] };
			f3 : Dispatch = { cond1 & cond2 => [*.foo] };
			f4 : Dispatch = { cond1 | cond2 => [*.foo] };
			f5 : Dispatch = { (cond1 & cond2) => [*.foo] };
			f6 : Dispatch = { cond1 & (cond2 | cond3) => [*.foo] };
			f7 : Dispatch = { !cond1 & !(!cond2 | !cond3) => [*.foo] }
	}
	
	filtermodule elmlists
	{
		inputfilters
			// multiple filter elements
			f1 : Dispatch = { 
				[*.foo], 
				[*.bar], 
				<inner.*> inner.* 
			};
			// matching pattern list
			f2 : Dispatch = {
				{[*.foo], [*.bar], [*.baz]} *.quux
			}			
	}
}
