using System;
using NUnit.Framework;

namespace Tests.ExitCodes
{
	[TestFixture] 
	public class IlWeaverExitCodesTest
	{
		public IlWeaverExitCodesTest()
		{
		}

		[Test] public void Success() 
		{
			Weavers.ExitCodes.IlWeaverExitCode exitcode = new Weavers.ExitCodes.IlWeaverExitCode(Weavers.ExitCodes.IlWeaverExitCodes.Success);
			Assert.AreEqual("The operation completed successfully.", exitcode.Message , "Invalid errorcode message.");
		}

		[Test] public void WeaveFileNotFound() 
		{
			Weavers.ExitCodes.IlWeaverExitCode exitcode = new Weavers.ExitCodes.IlWeaverExitCode(Weavers.ExitCodes.IlWeaverExitCodes.WeaveFileNotFound);
			Assert.AreEqual("File with the weave specification not found.", exitcode.Message , "Invalid errorcode message.");
		}	
	

	}
}
