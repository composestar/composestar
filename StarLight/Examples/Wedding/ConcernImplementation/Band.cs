using System;
using Composestar.StarLight.ContextInfo;

namespace Wedding.ConcernImplementations
{
	public class Band
	{
		public void Play(JoinPointContext jpc)
		{
			PlayBand();
		}

		public void PlayBand()
		{
			Console.WriteLine("The band played all night long");
		}
	}
}
