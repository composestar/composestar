using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace wedding.ConcernImplementations
{
	public class Party
	{
		public void startParty(ReifiedMessage rm)
		{
			rm.proceed();
			startParty();
		}

		public void startParty()
		{
			Console.WriteLine("They had a Party");
		}
	}
}
