concern StarDecoratorConcern
{
	filtermodule StarDecoratorFM
	{
		internals
			star		: Composestar.Patterns.Decorator.StarDecorator;
			
		conditions
			starOn		: Composestar.Patterns.Decorator.ActiveDecorators.starOn();
		
		inputfilters
			s			: Before = { starOn => [*.print] star.print }
	}
	superimposition
	{
		selectors
			component = { Component | classHasAnnotationWithName(Component, 'Composestar.Patterns.Decorator.Annotations.ConcreteComponent') };
			
		filtermodules
			component <- StarDecoratorFM;
	}
	
	implementation in JSharp by	StarDecorator as "StarDecorator.jsl"
	{
		package Composestar.Patterns.Decorator;
		
		import Composestar.StarLight.ContextInfo.JoinPointContext;
		
		public class StarDecorator{
	
			public void print(JoinPointContext jpc) {
				String s = (String) jpc.GetArgumentValue((short) 0);
				s = " *** " + s + " *** ";
				jpc.SetArgumentValue((short) 0, (Object) s);
			}
		}

	}
}
