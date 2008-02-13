using System;

namespace ErrorFilterTest
{
	class MainClass
	{
		[STAThread]
		static void Main(string[] args)
		{
			try
			{
				Subject s = new Subject();
				
				s.f1();				
				s.f2(); // should fail at runtime
			}
			catch (Exception e)
			{
				Console.WriteLine("Exception caught with message: " + e.Message);
				//Console.WriteLine(e.StackTrace);
			}
		}
	}
}
