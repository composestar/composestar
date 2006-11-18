concern Tracing {
 	filtermodule tracing {
 		inputfilters
 			tracing_filter : Tracing = { [*.*] }
 	}
 	superimposition {
 		selectors
 			allFunctions = { File | isFileWithName(File,'Calc') };
 		filtermodules
 			allFunctions <- tracing ;
 	}
}