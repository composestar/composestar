concern Overhead in Benchmark
{
	filtermodule FM
	{
		conditions
			shouldtrace : Benchmark.Benchmark.ShouldTraceCondition();
		inputfilters
			do_nothing : Empty = { True => [*.ExecuteFiltered] };
			do_trace : Tracing = { True => [*.ExecuteTracing] };
			do_reflectiontrace : ReflectionTracing = { True => [*.ExecuteReflectionTracing] };
			do_condition : Tracing = { shouldtrace => [*.ExecuteWithCFCondition] }
	}
	
	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C , 'Benchmark.Benchmark') };
		filtermodules	
			baseClass <- FM;
	}
}    