concern Filtermodules
{
	filtermodule LogModule
	{
		internals
			logger: SECRETTesting.LogACT;
		inputfilters
			log: Meta = { [*.a] logger.log }
	}
	
	filtermodule CryptModule
	{
		internals
			crypter: SECRETTesting.CryptACT;			
		inputfilters
			enc: Meta = { [*.a] crypter.encrypt }
	}
	
	filtermodule CrcModule
	{
		internals
			checker: SECRETTesting.CrcACT;			
		inputfilters
			crc: Meta = { [*.a] checker.checksum}
	}
	
	filtermodule SystemView
	{
		conditions
			
		inputfilters
			err: Error = { True => [*.*] }
	}

	superimposition
	{
		selectors
			selectees = { C | isClassWithName(C, 'SECRETTesting.MainRunner') };
		filtermodules
			selectees <- LogModule, CrcModule, CryptModule;
	}
}