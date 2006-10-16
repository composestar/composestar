concern TraceDocument in iTextSharp.Concerns
{

	filtermodule FM1
	{
		inputfilters
			trace: Tracing = { True => [*.*] };
			time: Profiling = { True => [*.*] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C , 'iTextSharp.text.pdf.PdfDocument') };
		filtermodules
			baseClass <- FM1;
	}
}