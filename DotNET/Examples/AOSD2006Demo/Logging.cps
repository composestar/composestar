concern Logging
{
	filtermodule Log
	{
		internals
			logger : EncryptionExample.Logger;
		inputfilters
			log : Meta = { [sendData]logger.log, [receiveData]logger.log }
	}

	superimposition
	{
		selectors
			loggableClasses = { Class | isInterfaceWithName(Interface, 'ProtocolLibrary.ProtocolObject'), implements(Class, Interface) };
		filtermodules
			loggableClasses <- Log;
	}
}