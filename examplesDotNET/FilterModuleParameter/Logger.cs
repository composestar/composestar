using System;
using Composestar.RuntimeCore.FLIRT.Message;

namespace FilterModuleParameter
{
	/// <summary>
	/// Summary description for Logger.
	/// </summary>

		public class Logger
		{
			public Logger()	{	}
	
			public void log()
			{
				Console.Out.WriteLine("logging...");
				//rm.proceed();
			}
			
			public void log(ReifiedMessage rm)
			{
				Console.Out.WriteLine("logging...");
				rm.proceed();
			}
		}
}
