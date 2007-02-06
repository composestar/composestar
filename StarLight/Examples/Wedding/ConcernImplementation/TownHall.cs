using System;
using Composestar.StarLight.ContextInfo;

namespace Wedding.ConcernImplementations
{
	public class TownHall
	{
		private static TownHall instance = new TownHall();

		public static TownHall GetInstance()
        {
            return instance;
		}

		private TownHall()
		{
		}

		public void NeedWeddingLicence(JoinPointContext jpc)
		{
			Person from = ((Person) jpc.Sender);
			if(!from.HasWeddingLicence())
			{
				GetWeddingLicence(from);
			}
		}
		
		public void GetWeddingLicence(Person person)
		{
			person.ReceiveWeddingsLicence();
			person.PayCash(100);
		}
	}
}
