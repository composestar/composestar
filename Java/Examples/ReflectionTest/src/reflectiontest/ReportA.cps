concern ReportA
{
	filtermodule FM1
	{
		internals
			inspct : reflectiontest.Inspector;
			b : reflectiontest.B;
		inputfilters
			adv : Before(target=inspct,selector='advice') = (true);
			foo : Dispatch = (selector == 'foo') { target = inspct; };
			tob : Dispatch = (selector $= b) { target = b; }
	}
	
	superimposition
	{
		selectors
			acls = { *=reflectiontest.A };
		filtermodules
			acls <- FM1;
	}
}