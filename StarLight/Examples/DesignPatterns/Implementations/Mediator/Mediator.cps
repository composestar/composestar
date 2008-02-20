concern MediatorConcern
{
	filtermodule ColleagueToMediator
	{
		externals
			mediator : Composestar.Patterns.Mediator.ConcreteMediator = Composestar.Patterns.Mediator.ConcreteMediator.instance();

		inputfilters
			//the action to take depends of the changed Colleague, so send to a meta filter.
			change1 : Before = { [*.clicked] mediator.colleagueChanged };
			change2 : After = { [*.clicked] mediator.cc }
	}
	filtermodule MediatorFM
	{
		conditions
			isButton1 : inner.isButton1();
			isButton2 : inner.isButton2();

		inputfilters
			b1 : Dispatch = {isButton1 => [*.cc] inner.setButton1};
			b2 : Dispatch = {isButton2 => [*.cc] inner.setButton2}
	}
	superimposition
	{
		selectors
																			
			colleague = { Colleague | classHasAnnotationWithName(Colleague, 'Composestar.Patterns.Mediator.Annotations.Colleague') };
			mediator = { Mediator | classHasAnnotationWithName(Mediator, 'Composestar.Patterns.Mediator.Annotations.Mediator') };
			
		filtermodules
			colleague <- ColleagueToMediator;
			mediator <- MediatorFM;
	}
}