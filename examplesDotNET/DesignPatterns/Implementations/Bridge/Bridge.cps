concern CreateBridge
{	
	filtermodule CreateBridge
	{
		internals
			impl2 : Composestar.Patterns.Bridge.CrossCapitalImplementation;
	
		conditions
			use1 : Composestar.Patterns.Bridge.DecisionMaker.use1();
	
        inputfilters
			use_impl1 : Dispatch = { use1 => inner.*};
			use_impl2 : Dispatch = { impl2.*}
	}
	superimposition
	{
		selectors
			implementor = { Implementor | classHasAnnotationWithName(Implementor, 'Composestar.Patterns.Bridge.Annotations.Implementor') };

		filtermodules
			implementor <- CreateBridge;
	}
}	

