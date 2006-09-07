using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace wedding.ConcernImplementations
{
	public class Band
	{
		public void play(ReifiedMessage rm)
		{
			rm.proceed();
			playBand();
		}

		public void playBand()
		{
			Console.WriteLine("The band played all night long");
		}
	}
}
