concern ParagraphTracing in iTextSharp.Concerns
{
	filtermodule FM1
	{
		inputfilters
			trace_add: Tracing = { True ~> [*.ToString] };
			time_add: Profiling = { True ~> [*.ToString] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'iTextSharp.text.Phrase') };
		filtermodules
			baseClass <- FM1;
	}
}