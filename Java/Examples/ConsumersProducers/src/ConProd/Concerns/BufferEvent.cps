concern BufferEvent in ConProd.Concerns
{
	filtermodule CatchEvents
	{
		internals
			notf : ConProd.Concerns.BufferConstraints;
		inputfilters
			f1 : Meta(target = notf) = ( selector == "produce" ) { filter.selector = "produceEvent"; }
				cor ( selector == "consume" ) { filter.selector = "consumeEvent"; }
	}
	
	superimposition
	{
		selectors
			buf = { C | isClassWithName(C, 'ConProd.Buffer') };
		filtermodules
			buf <- CatchEvents;
	}
}
