concern intexttest
{
	filtermodule intext
	{
		internals
			int: my_internal;
		externals
			ext: my_external;
		inputfilters
			to_int : Dispatch = ( selector == 'foo' ) { target = int; selector = 'realFoo'; }
		outputfilters
			to_ext : Dispatch = ( selector == 'bar' ) { target = ext; selector = 'realBar'; } 
	}
	
	superimposition
	{
		selectors
			main = { C | isClassWithName(C, 'main') };
		filtermodules
			main <- intext;
	}
}
