concern ErrorConcern {
	filtermodule error {
 		externals
 			error : Error;
 			st: StackFunctions;
 		conditions
 			stackEmpty : error.stackEmpty();
 			stackFull : error.stackFull();
 		inputfilters
 			error_filter : Error = { stackEmpty =>[*.ST_pop],
 				stackFull =>[*.ST_push]}
 	}
 	superimposition {
 		selectors
 			stackFunctions = { C | isClassWithName(C, 'StackFunctions') };
 		filtermodules
 			stackFunctions <- error ;
 	}
}