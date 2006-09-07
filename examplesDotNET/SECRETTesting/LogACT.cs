using System;
using Composestar.RuntimeCore.FLIRT.Message;
using Composestar.RuntimeDotNET;

namespace SECRETTesting
{
	public class LogACT
	{
		[Semantics("args.clean,args.read")]
		public void log(ReifiedMessage message)
		{
		}
	}
}