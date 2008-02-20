concern AbstractConstruction
{
	filtermodule Construct
	{
		internals
			director : Composestar.Patterns.Builder.Director;

		inputfilters
			dis : Dispatch = { [*.build] director.build }
	}
	superimposition
	{
		selectors
			ab = { Builders | classHasAnnotationWithName(AB , 'Composestar.Patterns.Builder.Annotations.Builder'), inheritsOrSelf(AB, Builders) };
			
		filtermodules
			ab <- Construct;
	}
}