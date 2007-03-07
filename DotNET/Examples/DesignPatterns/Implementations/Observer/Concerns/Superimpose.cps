//case specific concern
concern Superimpose in Composestar.Patterns.Observer{
	superimposition{
		selectors
			point = { C | isClassWithName(C, 'Composestar.Patterns.Observer.Point') };
			screen = { C | isClassWithName(C, 'Composestar.Patterns.Observer.Screen') };

		filtermodules
			//add and remove observers; is generic
			point <- AddRemoveObservers::AddRemoveObservers;
			screen <- AddRemoveObservers::AddRemoveObservers;

			//notify observers; is case specific
			point <- NotifyObservers::NotifyPosition;
			point <- NotifyObservers::NotifyColor;
			screen <- NotifyObservers::NotifyDisplay;

			//perform observer action; is case specific
			screen <- ObserverUpdate::Action;
	}
}
