//generic concern
concern ObserverUpdate in Composestar.Patterns.Observer{
	filtermodule Action
	{
		internals
			observer : Composestar.Patterns.Observer.Observer;

		inputfilters
			refresh : Dispatch = { [*.refresh] observer.refresh}
	}
}
