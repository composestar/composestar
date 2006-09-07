using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace wedding.ConcernImplementations
{
	public class Catering
	{
		public void serveDinner(ReifiedMessage rm)
		{
			rm.proceed();
			serveDinner();
		}

		public void serveDinner()
		{
			Console.WriteLine("They had a dinner");
		}
	}
}
