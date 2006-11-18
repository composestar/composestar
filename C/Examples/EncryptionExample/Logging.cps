concern Logging
{
	filtermodule Log
	{
		internals
			logger : Logger;
		inputfilters
			log : Meta = { [*.clientReceiveData] logger.loggerLog, [*.clientSendData] logger.loggerLog } 		
	}
	
	superimposition
	{	
		selectors
			loggableClasses = { Class | isClass(Class)};
		filtermodules
			loggableClasses <- Log;
	}
}