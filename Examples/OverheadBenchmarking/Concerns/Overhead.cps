concern Overhead in Benchmark
{
	filtermodule FM
	{
		inputfilters
			do_nothing : Empty = {True => [*.ExecuteFiltered] }

	}
	
	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C , 'Benchmark.Benchmark') };
		filtermodules	
			baseClass <- FM;
	}
}    