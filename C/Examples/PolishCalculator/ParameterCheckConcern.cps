concern ParameterCheckConcern {
 filtermodule Paramcheck {
 	externals
 		calc:CalculateFunctions;
 	inputfilters
 		paramcheck_filter : Parametercheck = { [calc.*] }
 }
 superimposition {
 	selectors
 		calcFunctions = { C | isClassWithName(C, 'CalculateFunctions') };
 	filtermodules
 		calcFunctions <- Paramcheck ;
 }
}