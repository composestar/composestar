concern TraceDocument in iTextSharp.Concerns
{

	filtermodule FM1
	{
		inputfilters
			trace_open: Tracing = { True => [*.Open] };
			time_open: Profiling = { True => [*.Open] };
			
			trace_close: Tracing = { True => [*.Close] };
			time_close: Profiling = { True => [*.Close] };
			
			trace_add: Tracing = { True => [*.Add] };
			time_add: Profiling = { True => [*.Add] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isNamespaceWithName(NS,'iTextSharp.text.pdf') , namespaceHasClass(NS, C) };
		filtermodules
			baseClass <- FM1;
	}
}