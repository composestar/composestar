concern EncryptDecryptArgs in RuBCoDeTest.TestCase
{
	filtermodule EncFltUsage
	{
		conditions
			enc1 : RuBCoDeTest.TestCase.MainClass.encrypt1();
			enc2 : RuBCoDeTest.TestCase.MainClass.encrypt2();
		inputfilters
			security : EncryptArgs = { enc1 => [*.sendMessage], enc1 => [*.archiveMessage], enc1 => [*.discardMessage] };
			encargs : EncryptArgs = { enc2 => [*.sendMessage], enc2 => [*.archiveMessage] }
	}
  
	superimposition
	{
		selectors
			tc = { C |isClassWithName(C, 'RuBCoDeTest.TestCase.TargetClass') };
		filtermodules
			tc <- EncFltUsage;
	}
}
