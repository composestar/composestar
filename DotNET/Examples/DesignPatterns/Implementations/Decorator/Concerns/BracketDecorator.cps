concern BracketDecoratorConcern
{
	filtermodule BracketDecoratorFM
	{
		internals
			bracket		: Composestar.Patterns.Decorator.BracketDecorator;

		conditions
			bracketOn	: Composestar.Patterns.Decorator.ActiveDecorators.bracketOn();

		inputfilters
			b			: Meta = { bracketOn => [*.print] bracket.print }
	}
	superimposition
	{
		selectors
			component = { Component | classHasAnnotationWithName(Component, 'Composestar.Patterns.Decorator.Annotations.ConcreteComponent') };

		filtermodules
			component <- BracketDecoratorFM;
	}
	implementation in JSharp by	BracketDecorator as	"BracketDecorator.jsl"
	{
		package Composestar.Patterns.Decorator;

		import Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage;

		public class BracketDecorator{

			public void print(ReifiedMessage message) {
				String s = (String) message.getArg(0);
				s = "["+s+"]";
				message.setArg(0,s);
			}
		}
	}
}
