using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace wedding.ConcernImplementations
{
	public class TaxOffice
	{
		private static TaxOffice instance = new TaxOffice();

		public static TaxOffice getInstance()
		{
			return instance;
		}

		private TaxOffice()
		{
		}

		public void payIncomeTax(ReifiedMessage rm)
		{
			int ammount = (int) rm.getArg(0);
			if(ammount > 2000)
			{
				Console.WriteLine("Because the income is more then 2000, he needs to pay income tax");
				rm.setArg(0,ammount - calculateTax(ammount));
			}
		}

		public int calculateTax(int ammount)
		{
			int tax = (int) (0.18 * ammount);
			Console.WriteLine("Need to pay " + tax + " in taxes");
			return tax;
		}
	}
}
