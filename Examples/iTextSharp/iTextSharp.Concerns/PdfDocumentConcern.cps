concern TraceDocument in iTextSharp.Concerns
{

	filtermodule FM1
	{
		inputfilters
			trace: Tracing = { True => [*.*] };
			time: Profiling = { True => [*.*] }
	}
	
	filtermodule FM2
	{
		inputfilters
			empty: Empty = { True => [*.*] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C , 'iTextSharp.text.pdf.PdfDocument') };
		filtermodules
			baseClass <- FM1;
	}
}