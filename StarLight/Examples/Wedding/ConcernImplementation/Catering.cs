using System;
using Composestar.StarLight.ContextInfo;

namespace Wedding.ConcernImplementations
{
	public class Catering
	{
		public void ServeDinner(JoinPointContext jpc)
		{
			Console.WriteLine("They had a dinner");
		}
	}
}
