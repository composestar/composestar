concern profiling
{
	filtermodule profileall
	{
		inputfilters
			prof : Profile = { True ~> [*.main] }
	}
	
	superimposition
	{
		selectors
			allclasses = { C | isClass(C) };
		filtermodules
			allclasses <- profileall;
	}
}
