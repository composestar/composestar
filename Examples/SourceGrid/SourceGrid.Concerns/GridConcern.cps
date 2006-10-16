concern GridConcern in SourceGrid.Concerns
{
	filtermodule FM1
	{
		inputfilters
			trace_insert: Tracing = { True => [*.InsertCell] };
			time_inserrt: Profiling = { True => [*.InsertCell] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'SourceGrid.Grid') };
		filtermodules
			baseClass <- FM1;
	}
}