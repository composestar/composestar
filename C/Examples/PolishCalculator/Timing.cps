concern Timing {
 	filtermodule timing {
 		inputfilters
 			timing_filter : Timing = { [*.*] }
 	}
 	superimposition {
 		selectors
 			allFunctions = { File | isFileWithName(File,'Calc') };
 		filtermodules
 			allFunctions <- timing ;
 	}
}