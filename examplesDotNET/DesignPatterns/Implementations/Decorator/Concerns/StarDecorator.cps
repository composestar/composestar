concern StarDecorator
{
	filtermodule StarDecorator
	{
		internals
			star		: Composestar.Patterns.Decorator.StarDecorator;
			
		conditions
			starOn		: Composestar.Patterns.Decorator.ActiveDecorators.starOn();
		
		inputfilters
			s			: Meta = { starOn 	 => [*.print] star.print }
	}
	superimposition
	{
		selectors
			component = { Component | classHasAnnotationWithName(Component, 'Composestar.Patterns.Decorator.Annotations.ConcreteComponent') };
			
		filtermodules
			component <- StarDecorator;
	}
	implementation in JSharp by	StarDecorator as "StarDecorator.jsl"
	{
		package Composestar.Patterns.Decorator;

		import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

		public class StarDecorator{
	
			public void print(ReifiedMessage message) {
				String s = (String) message.getArg(0);
				s = " *** " + s + " *** ";
				message.setArg(0,s);
			}
		}

	}
}
