concern EncryptionConcern
{
	filtermodule EncryptModule
	{		
		inputfilters
			encrypt: Encryption = { [protocolAddTimeStamp] }//*.encryptorEncrypt 
	}

	filtermodule DecryptModule
	{		
		inputfilters
			decrypt: Decryption = { [protocolReceiveData]  }//*.encryptorDecrypt
	}

	superimposition
	{
		selectors
			stream = { Class | isClassWithName(Class,'Protocol') };
		filtermodules
			stream <- EncryptModule;
			stream <- DecryptModule;
	}
	
}