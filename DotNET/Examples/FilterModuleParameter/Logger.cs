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
		/*	
			public void log()
			{
				Console.Out.WriteLine("logging...");
			}
		*/
			public void log(ReifiedMessage rm)
			{
				Console.Out.WriteLine("logging: " + ToString(rm));
				rm.proceed();
			}

			private String ToString(ReifiedMessage rm)
			{
				return rm.getTarget() + "." + rm.getSelector();
			}
		}
}
