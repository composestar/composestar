concern $safeitemname$
{
	filtermodule FilterModuleName
	{
		internals		// declare used internal (per instance) objects
		externals		// declare used external (global) objects
		conditions		// declare used conditions
		inputfilters	// define the inputfilters
		outputfilters	// define the outputfilters
	}
	
	superimposition
	{
		selectors		// selects classnames with queries
		conditions		// bind the listed conditions to the context specified by selectors
		methods			// bind the listed methods to the context specified by selectors
		filtermodules	// superimpose the list filtermodules at the locations
		annotations		// bind the listed annotations to the context specified by selectors
		constraints		// ordering of the filters
	}
}    