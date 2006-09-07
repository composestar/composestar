concern Rot13Concern
{
	filtermodule Rot13Filter
	{
		inputfilters
			rotate: Rot13 = { [*.ReadLine] }
	}

	superimposition
	{
		selectors
			stream = { *=Rot13Filter.MyReader };
		filtermodules
			stream <- Rot13Filter;
			// Uncomment the next line to rotate the text twice and make it readable again.
			// stream <- Rot13Filter;
	}
}
