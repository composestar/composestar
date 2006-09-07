concern Superimpose
{
	superimposition
	{
		selectors
			context = { Context | classHasAnnotationWithName(Context, 'Composestar.Patterns.State.Annotations.Context') };

		filtermodules
			context <- StateForwarding::forwarding;
			context <- StateTransitions::transitions;
	}	
}	