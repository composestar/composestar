concern AdapterConcern
{
	filtermodule AdapterFM
	{
		internals
			adapter : Composestar.Patterns.Adapter.Adapter;

		inputfilters
			adapt_write   : Dispatch	= { [*.write] *.printToSystemOut };
			adapt_arg     : Before		= { [*.printText] adapter.printDefaultText };
			adapt_square1 : Before		= { [*.printSquare] inner.printHorizontal };
			adapt_square2 : Dispatch	= { [*.printSquare] inner.printVertical };
			adapt_square3 : After		= { [*.printSquare] inner.printHorizontal }
	}
	superimposition
	{
		selectors
			adaptee = { Adaptee | classHasAnnotationWithName(Adaptee, 'Composestar.Patterns.Adapter.Annotations.Adaptee') };
			
		filtermodules
			adaptee <- AdapterFM;
	}
}
