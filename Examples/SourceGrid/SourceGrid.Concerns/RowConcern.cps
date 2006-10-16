concern RowConcern in SourceGrid.Concerns
{
	filtermodule FM1
	{
		inputfilters
			trace_insert: Tracing = { True => [*.Insert] };
			time_inserrt: Profiling = { True => [*.Insert] }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'SourceGrid.GridRows') };
		filtermodules
			baseClass <- FM1;
	}
}