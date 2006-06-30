concern DollarDecorator
{
	filtermodule DollarDecorator
	{
		internals
			dollar		: Composestar.Patterns.Decorator.DollarDecorator;
			
		conditions
			dollarOn	: Composestar.Patterns.Decorator.ActiveDecorators.dollarOn();
		
		inputfilters
			d			: Meta = { dollarOn => [*.print] dollar.print }
	}
	superimposition
	{
		selectors
			component = { Component | classHasAnnotationWithName(Component, 'Composestar.Patterns.Decorator.Annotations.ConcreteComponent') };
			
		filtermodules
			component <- DollarDecorator;
	}
	implementation in JSharp by	DollarDecorator as "DollarDecorator.jsl"
	{
		package Composestar.Patterns.Decorator;

		import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

		public class DollarDecorator
		{
			public void print(ReifiedMessage message) 
			{
				String s = (String) message.getArg(0);
				s = "$"+s+"$";
				message.setArg(0,s);
			}
		}
	}
}
