concern trace
{
	filtermodule traceall
	{
		inputfilters
			tracer : Trace = { True ~> [*.main] }
	}
	
	superimposition
	{
		selectors
			allclasses = { C | isClass(C) };
		filtermodules
			allclasses <- traceall;
	}
}
