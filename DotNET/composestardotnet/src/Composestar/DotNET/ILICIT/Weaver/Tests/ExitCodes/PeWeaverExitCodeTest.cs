using System;
using NUnit.Framework;

namespace Tests.ExitCodes
{
	[TestFixture] 
	public class PeWeaverExitCodesTest
	{
		public PeWeaverExitCodesTest()
		{
		}

		[Test] public void Success() 
		{
			Weavers.ExitCodes.PeWeaverExitCode exitcode = new Weavers.ExitCodes.PeWeaverExitCode(0);
			Assert.AreEqual("The operation completed successfully.", exitcode.Message , "Expected Failure.");
		}
	}
}
