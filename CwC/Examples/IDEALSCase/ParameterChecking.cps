concern ParameterChecking
{
	filtermodule check
	{
		internals
			checker : ParameterChecker;
		conditions
			inputwrong : checker.inputParametersAreInvalid();
			outputwrong : checker.outputParametersAreInvalid();
		inputfilters
			paramcheckfilter : ParameterChecking = { inputwrong | outputwrong => [*.compare_data] *.* }
	}

	superimposition
	{
		selectors
			sel = {Class | isClassWithName(Class, 'CC.CX.FS')};
		filtermodules
			sel <- check;
	}
}
