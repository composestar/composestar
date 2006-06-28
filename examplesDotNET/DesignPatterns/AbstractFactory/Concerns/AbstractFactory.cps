concern AbstractFactory
{
	//Evolution 2: add Coloredfactory and use it
	//Contains createInputField method directly because this method was added in evolution 1
	filtermodule ConcreteFactory
	{
		internals
			cf1 : Composestar.Patterns.AbstractFactory.ColoredFactory;

		inputfilters
			concreteFactory : Dispatch = { cf1.* }
	}
	superimposition
	{
		selectors
			af = { C | classHasAnnotationWithName(C, 'Composestar.Patterns.AbstractFactory.Annotations.AbstractFactory') };
			
		filtermodules
			af <- ConcreteFactory;
	}
}
