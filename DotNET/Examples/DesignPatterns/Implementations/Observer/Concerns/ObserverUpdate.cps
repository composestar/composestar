//generic concern
concern ObserverUpdate{
	filtermodule Action
	{
		internals
			observer : Composestar.Patterns.Observer.Observer;

		inputfilters
			refresh : Dispatch = { [*.refresh] observer.refresh}
	}
}