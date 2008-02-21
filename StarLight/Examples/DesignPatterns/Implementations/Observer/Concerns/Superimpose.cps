//case specific concern
concern Superimpose in Composestar.Patterns.Observer{
	superimposition{
		selectors
			point = { C | isClassWithName(C, 'Composestar.Patterns.Observer.Point') };
			screen = { C | isClassWithName(C, 'Composestar.Patterns.Observer.Screen') };

		filtermodules
			//add and remove observers; is generic
			point <- Composestar.Patterns.Observer.AddRemoveObservers::AddRemoveObservers;
			screen <- Composestar.Patterns.Observer.AddRemoveObservers::AddRemoveObservers;

			//notify observers; is case specific
			point <- Composestar.Patterns.Observer.NotifyObservers::NotifyPosition;
			point <- Composestar.Patterns.Observer.NotifyObservers::NotifyColor;
			screen <- Composestar.Patterns.Observer.NotifyObservers::NotifyDisplay;

			//perform observer action; is case specific
			screen <- Composestar.Patterns.Observer.ObserverUpdate::Action;
	}
}
