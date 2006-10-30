concern logging in FilterModuleParameter
{
	filtermodule log(?logger, ??walkfunction)
	{
		internals
			logger : ?logger;
		inputfilters
			m : Meta = { [*.??walkfunction] logger.log }
	}
}