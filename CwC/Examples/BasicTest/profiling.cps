concern profiling
{
	filtermodule profileall
	{
		inputfilters
			prof : Profile = { [*.nsieve] }
	}
	
	superimposition
	{
		selectors
			allclasses = { C | isClass(C) };
		filtermodules
			allclasses <- profileall;
	}
}
