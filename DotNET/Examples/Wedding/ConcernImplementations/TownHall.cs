using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace wedding.ConcernImplementations
{
	public class TownHall
	{
		private static TownHall instance = new TownHall();

		public static TownHall getInstance()
		{
			return instance;
		}

		private TownHall()
		{
		}

		public void needWeddingLicence(ReifiedMessage rm)
		{
			rm.resume();
			Person from = ((Person) rm.getSender());
			if(!from.hasWeddingLicence())
			{
				getWeddingLicence(from);
			}
		}
		
		public void getWeddingLicence(Person person)
		{
			person.receiveWeddingsLicence();
			person.payCash(100);
		}
	}
}
