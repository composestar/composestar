concern Creator
{	
	filtermodule Creator
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
			creators <- Creator;
	}
}	

