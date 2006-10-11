concern TraceDocument in iTextSharp.Concerns
{

	filtermodule FM1
	{
	  externals
	    logger : iTextSharp.Concerns.Logger = iTextSharp.Concerns.Logger.Instance();
		inputfilters
			documentOpened : After = { True => [*.Open] logger.Log }
	}

	superimposition
	{
		selectors
			baseClass = { C | isClassWithName(C, 'iTextSharp.text.Document') };
		filtermodules
			baseClass <- FM1;
	}
}