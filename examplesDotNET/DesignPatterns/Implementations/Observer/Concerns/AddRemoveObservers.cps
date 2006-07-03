concern AddRemoveObservers{
	filtermodule AddRemoveObservers
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			add : Dispatch = { [*.addObserver] subject.addObserver};
			remove : Dispatch = { [*.removeObserver] subject.removeObserver}
	}
}