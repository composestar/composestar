using System;
using Composestar.StarLight.ContextInfo;

namespace FilterModuleOrderingConstraints
{
	public class FilterPrinter
	{
		public FilterPrinter()
		{
		}

        public void printFM1(JoinPointContext jpc)
		{
			Console.WriteLine("First(FM1)");
		}

        public void printFM2(JoinPointContext jpc)
		{
			Console.WriteLine("Fourth(FM2)");
		}

        public void printFM3(JoinPointContext jpc)
		{
			Console.WriteLine("Second(FM3)");
		}

        public void printFM4(JoinPointContext jpc)
		{
			Console.WriteLine("Third(FM4)");
		}
	}
}
