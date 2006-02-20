using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace wedding.ConcernImplementations
{
	public class Pocket
	{
		private static Pocket instance = new Pocket();

		public static Pocket getInstance()
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

		public void payCash(int ammount)
		{
			Console.WriteLine("Need to pay " + ammount);
			if(!hasCash(ammount))
			{
				sellCar();
			}
			Console.WriteLine("Payed the " + ammount);
			cash -= ammount;
			Console.WriteLine("Still have " + cash);
		}

		public void recieveCash(int ammount)
		{
			Console.WriteLine("Recieved cash " + ammount);
			cash += ammount;
		}

		public bool hasCash(int ammount)
		{
			return cash >= ammount;
		}

		public void sellCar()
		{
			if(hasCar)
			{
				Console.WriteLine("Selling the car for 3000");
				hasCar = false;
				recieveCash(3000);
			}
			else
			{
				Console.WriteLine("Don't have a car to sell");
				throw new Exception("Dont have money enough");
			}
		}

		public void payBand(ReifiedMessage rm)
		{
			Console.WriteLine("The band wants 500");
			payCash(500);
		}

		public void payCatering(ReifiedMessage rm)
		{
			Console.WriteLine("The catering wants 1860");
			payCash(1860);
		}

	}
}
