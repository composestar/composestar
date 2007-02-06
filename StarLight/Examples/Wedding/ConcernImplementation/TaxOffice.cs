using System;
using Composestar.StarLight.ContextInfo;

namespace Wedding.ConcernImplementations
{
	public class TaxOffice
	{
		private static TaxOffice instance = new TaxOffice();

		public static TaxOffice GetInstance()
		{
			return instance;
		}

		private TaxOffice()
		{
		}

		public void PayIncomeTax(JoinPointContext jpc)
		{
			int ammount = (int) jpc.GetArgumentValue(0);
			if(ammount > 2000)
			{
				Console.WriteLine("Because the income is more then 2000, he needs to pay income tax");
				jpc.SetArgumentValue(0,ammount - CalculateTax(ammount));
			}
		}

		public int CalculateTax(int ammount)
		{
			int tax = (int) (0.18 * ammount);
			Console.WriteLine("Need to pay " + tax + " in taxes");
			return tax;
		}
	}
}
