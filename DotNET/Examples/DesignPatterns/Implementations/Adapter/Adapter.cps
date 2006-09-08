concern AdapterConcern
{
	filtermodule AdapterFM
	{
		internals
			adapter : Composestar.Patterns.Adapter.Adapter;

		inputfilters
			adapt_write  : Dispatch = { [*.write] *.printToSystemOut };
			adapt_arg    : Meta		= { [*.printText] adapter.printDefaultText };
			adapt_square : Dispatch = { [*.printSquare] #(inner.printHorizontal;inner.printVertical;inner.printHorizontal) }
	}
	superimposition
	{
		selectors
			adaptee = { Adaptee | classHasAnnotationWithName(Adaptee, 'Composestar.Patterns.Adapter.Annotations.Adaptee') };
			
		filtermodules
			adaptee <- AdapterFM;
	}
}