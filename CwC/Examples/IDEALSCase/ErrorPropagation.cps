concern ErrorPropagationConcern
{
	filtermodule propagate
	{
		inputfilters
			errorpropagationfilter : ErrorPropagation = { [*.*] }
	}

	superimposition
	{
		selectors
			sel = {Class | isClass(Class)};
		filtermodules
			sel <- propagate;
	}
}
