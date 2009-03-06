concern ReportB
{
	filtermodule FM1
	{
		internals
			inspct : reflectiontest.Inspector;
		inputfilters
			adv : Before(target=inspct,selector='advice') = (true);
			mta : Meta(target=inspct,selector='advice') = (true);
			foo : Dispatch = (selector == 'foo') { target = inspct; }
	}
	
	superimposition
	{
		selectors
			bcls = { *=reflectiontest.B };
		filtermodules
			bcls <- FM1;
	}
}
