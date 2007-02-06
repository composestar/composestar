using System;
using Composestar.StarLight.ContextInfo;

namespace Wedding.ConcernImplementations
{
	public class Party
	{
		public void StartParty(JoinPointContext jpc)
		{
			Console.WriteLine("They had a Party");
		}
	}
}
