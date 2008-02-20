concern BracketDecoratorConcern
{
	filtermodule BracketDecoratorFM
	{
		internals
			bracket		: Composestar.Patterns.Decorator.BracketDecorator;

		conditions
			bracketOn	: Composestar.Patterns.Decorator.ActiveDecorators.bracketOn();

		inputfilters
			b			: Before = { bracketOn => [*.print] bracket.print }
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

		import Composestar.StarLight.ContextInfo.JoinPointContext;

		public class BracketDecorator{

			public void print(JoinPointContext jpc) {
				String s = (String) jpc.GetArgumentValue((short) 0);
				s = "[" + s + "]";
				jpc.SetArgumentValue((short) 0, (Object) s);
			}
		}
	}
}
