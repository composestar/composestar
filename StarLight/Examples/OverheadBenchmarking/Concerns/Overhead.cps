concern Overhead in Benchmark
{
	filtermodule FM
	{
		inputfilters
			do_nothing : Empty = {True => [*.ExecuteFiltered] };
			do_trace : Tracing = {True => [*.ExecuteTracing] };
			do_reflectiontrace : ReflectionTracing = {True => [*.ExecuteReflectionTracing] }
	}
	
	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C , 'Benchmark.Benchmark') };
		filtermodules	
			baseClass <- FM;
	}
}    