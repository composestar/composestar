concern Logging in SecretAOSDExample
{
	filtermodule Log
	{
		internals
			logger : SecretAOSDExample.Logger;
		inputfilters
			log : Meta = { [sendData]logger.log, [receiveData]logger.log }
	}

	superimposition
	{
		selectors
			loggableClasses = { Class | isClassWithName(Class,'SecretAOSDExample.Protocol') };
		filtermodules
			loggableClasses <- Log;
	}
}