using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace FilterModuleOrderingConstraints
{
	public class FilterPrinter
	{
		public FilterPrinter()
		{
			
		}

		public void printFM1(ReifiedMessage rm)
		{
			Console.WriteLine("First(FM1)");
			rm.proceed();
		}

		public void printFM2(ReifiedMessage rm)
		{
			Console.WriteLine("Fourth(FM2)");
			rm.proceed();
		}

		public void printFM3(ReifiedMessage rm)
		{
			Console.WriteLine("Second(FM3)");
			rm.proceed();
		}

		public void printFM4(ReifiedMessage rm)
		{
			Console.WriteLine("Third(FM4)");
			rm.proceed();
		}
	}
}
