concern CreatorConcern
{	
	filtermodule CreatorFM
	{
		internals
			creator : Composestar.Patterns.FactoryMethod.Creator;
			
		inputfilters
			dis	 : Dispatch = { creator.* }
	}
	superimposition
	{
		selectors
			creators = { Creators | classHasAnnotationWithName(Creator, 'Composestar.Patterns.FactoryMethod.Annotations.Creator'), inheritsOrSelf(Creator,Creators) };

		filtermodules
			creators <- CreatorFM;
	}
}	

