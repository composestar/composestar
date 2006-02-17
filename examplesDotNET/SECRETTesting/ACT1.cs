using System;
using Composestar.Runtime.FLIRT.message;
using Composestar.Reasoning;

namespace SECRETTesting
{
	public class ACT1
	{
		[Scenario("target.w;target.l;message.f")]
		public void doLog(ReifiedMessage message)
		{
			message.fire();
		}

		public bool loggingEnabled()
		{
			return true;
		}
	}
}