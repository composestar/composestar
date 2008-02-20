concern DollarDecoratorConcern
{
	filtermodule DollarDecoratorFM
	{
		internals
			dollar		: Composestar.Patterns.Decorator.DollarDecorator;
			
		conditions
			dollarOn	: Composestar.Patterns.Decorator.ActiveDecorators.dollarOn();
		
		inputfilters
			d			: Before = { dollarOn => [*.print] dollar.print }
	}
	superimposition
	{
		selectors
			component = { Component | classHasAnnotationWithName(Component, 'Composestar.Patterns.Decorator.Annotations.ConcreteComponent') };
			
		filtermodules
			component <- DollarDecoratorFM;
	}
	implementation in JSharp by	DollarDecorator as "DollarDecorator.jsl"
	{
		package Composestar.Patterns.Decorator;

		import Composestar.StarLight.ContextInfo.JoinPointContext;

		public class DollarDecorator
		{
			public void print(JoinPointContext jpc) 
			{
				String s = (String) jpc.GetArgumentValue((short) 0);
				s = "$" + s + "$";
				jpc.SetArgumentValue((short) 0, (Object) s);
			}
		}
	}
}
