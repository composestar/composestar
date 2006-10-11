using System;
using Composestar.StarLight.ContextInfo;

namespace iTextSharp.Concerns
{
	public class Logger
	{
		private static Logger instance = null;
		
		private Logger()
		{
		}
		
		public static Logger Instance()
		{
			if (instance == null) instance = new Logger();
			
			return instance;
		}

    public void Log(JoinPointContext context)
    {
        Console.WriteLine("after " + context.MethodName);


    }
	}
}
