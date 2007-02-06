using System;
using Composestar.StarLight.ContextInfo;

namespace Wedding.ConcernImplementations
{
	public class Pocket
	{
		private static Pocket instance = new Pocket();

        public static Pocket GetInstance()
        {
            return instance;
        }

		private int cash;
		private bool hasCar;

		public Pocket()
		{
			cash =0;
			hasCar = true;
		}

		public void PayCash(int ammount)
		{
			Console.WriteLine("Need to pay " + ammount);
			if(!HasCash(ammount))
			{
				SellCar();
			}
			Console.WriteLine("Payed the " + ammount);
			cash -= ammount;
			Console.WriteLine("Still have " + cash);
		}

		public void RecieveCash(int ammount)
		{
			Console.WriteLine("Recieved cash " + ammount);
			cash += ammount;
		}

		public bool HasCash(int ammount)
		{
			return cash >= ammount;
		}

		public void SellCar()
		{
			if(hasCar)
			{
				Console.WriteLine("Selling the car for 3000");
				hasCar = false;
				RecieveCash(3000);
			}
			else
			{
				Console.WriteLine("Don't have a car to sell");
				throw new Exception("Dont have money enough");
			}
		}

		public void PayBand(JoinPointContext jpc)
		{
			Console.WriteLine("The band wants 500");
			PayCash(500);
		}

		public void PayCatering(JoinPointContext jpc)
		{
			Console.WriteLine("The catering wants 1860");
			PayCash(1860);
		}

	}
}
