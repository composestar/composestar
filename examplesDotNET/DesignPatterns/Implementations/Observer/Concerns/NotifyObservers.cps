//notification of observers for all subjects is grouped
//case specific concern
concern NotifyObservers{
	filtermodule NotifyDisplay
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			notifyDisplay : Meta = { [*.display] subject.notifyObservers  }
	}
	filtermodule NotifyPosition
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			notifyX : Meta = { [*.setX] subject.notifyObservers  };
			notifyY : Meta = { [*.setY] subject.notifyObservers  }
	}
	filtermodule NotifyColor
	{
		externals
			subject : Composestar.Patterns.Observer.Subject = Composestar.Patterns.Observer.Subject.instance();

		inputfilters
			notifyColor : Meta = { [*.setColor] subject.notifyObservers  }
	}
}

