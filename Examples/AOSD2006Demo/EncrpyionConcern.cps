concern EncryptionConcern
{
	filtermodule EncryptModule
	{		
		inputfilters
			encrypt: Encryption = { [sendData] }
	}

	filtermodule DecryptModule
	{		
		inputfilters
			decrypt: Decryption = { [receiveData] }
	}

	superimposition
	{
		selectors
			stream = { Class | isInterfaceWithName(Interface, 'ProtocolLibrary.ProtocolObject'), implements(Class, Interface) };
			//stream = { Class | isClassWithName(Class,'System.DateTime') };
		filtermodules
			stream <- EncryptModule;
			stream <- DecryptModule;
	}
}