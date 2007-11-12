concern EncryptDecryptArgs in RuBCoDeTest.TestCase
{
	filtermodule EncFltUsage
	{
		conditions
			doEnc : RuBCoDeTest.TestCase.MainClass.doEncrypt();
		inputfilters
			encargs : EncryptArgs = { doEnc => [*.*] }
	}
  
	superimposition
	{
		selectors
			tc = { C |isClassWithName(C, 'RuBCoDeTest.TestCase.TargetClass') };
		filtermodules
			tc <- EncFltUsage;
	}
}