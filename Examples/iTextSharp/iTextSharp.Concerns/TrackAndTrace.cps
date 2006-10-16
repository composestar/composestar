concern TrackAndTrace in iTextSharp.Concerns
{
	filtermodule Tracking
	{
		inputfilters
			trace: Tracing = { True => [*.*] }
	}

	filtermodule Timing
	{
		inputfilters
			time: Profiling = { True => [*.*] }
	}

	superimposition
	{
		// Superimposing on entire namespaces makes compiling rather slow
		selectors
			baseClass = { C | isNamespaceWithName(NS,'iTextSharp.text.pdf') , namespaceHasClass(NS, C) };
		filtermodules
			baseClass <- Tracking, Timing;
	}
}