//notification of observers for all subjects is grouped
//case specific concern
concern NotifyObservers in Composestar.Patterns.Observer{
	filtermodule NotifyDisplay
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			notifyDisplay : After = { [*.display] subject.notifyObservers  }
	}
	filtermodule NotifyPosition
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			notifyX : After = { [*.setX] subject.notifyObservers  };
			notifyY : After = { [*.setY] subject.notifyObservers  }
	}
	filtermodule NotifyColor
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			notifyColor : After = { [*.setColor] subject.notifyObservers  }
	}
}

