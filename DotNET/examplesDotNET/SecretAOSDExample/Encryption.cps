concern Encryption in SecretAOSDExample
{
	filtermodule Encrypt
	{
		internals
			encryptor : SecretAOSDExample.Encryptor;
		inputfilters
			encryption : Meta = { [sendData]encryptor.encrypt}
	}

	filtermodule Decrypt
	{
		internals
			encryptor : SecretAOSDExample.Encryptor;
		inputfilters
			decryption : Meta = { [receiveData]encryptor.decrypt}
	}

	superimposition
	{
		selectors
			encryptableClasses = { Class | isClassWithName(Class,'SecretAOSDExample.Protocol') };
		filtermodules
			encryptableClasses <- Encrypt, Decrypt;
	}
}