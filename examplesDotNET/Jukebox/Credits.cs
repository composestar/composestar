using System;
using Composestar.RuntimeDotNET;
using Composestar.RuntimeCore.FLIRT.Message;

namespace Jukebox
{
	public class Credits
	{
		private static Credits credits;
		int numCredits = 0;

		public static Credits instance()
		{
			if( credits == null )
				credits = new Credits();
			return credits;
		}
		
		[Semantics("song.credit")]
		public void withdraw(ReifiedMessage message)
		{
			this.numCredits--;
			Console.WriteLine("Payed, " + numCredits + " left"); 
			message.resume();
		}

		private Credits()
		{
		}

		public void pay(int num)
		{
			numCredits += num;
			Console.WriteLine("Added, " + num + " credits ");
		}

		public bool payed()
		{
			return numCredits > 0;
		}
	}
}
