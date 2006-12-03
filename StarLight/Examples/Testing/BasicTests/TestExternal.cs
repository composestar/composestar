using System;
using Composestar.StarLight.ContextInfo;

namespace BasicTests
{
	public class TestExternal : TestsBase
	{
		private static TestExternal instance = null;
		
		private TestExternal()
		{
		}
		
		public static TestExternal getInstance()
		{
			if (instance == null) instance = new TestExternal();
			
			return instance;
		}

		public void externalMe()
		{
			report("externalMe");
		}

		public void before(JoinPointContext context)
		{
			report("before " + context.StartSelector);

			if (context.StartSelector.Equals("func4"))
			{
				int arg = (int) context.GetArgumentValue(0);
				arg = arg + 1;
				context.AddArgument(0, arg.GetType() , arg);
			}
			else if (context.StartSelector.Equals("func9"))
			{
				object arg = context.GetArgumentValue(0);
				Console.WriteLine("\targ=" + (arg == null ? "null" : "'" + arg + "'"));
			}
			else if (context.StartSelector.Equals("func10"))
			{
				int arg = (int)context.GetArgumentValue(0);
				Console.WriteLine("BEFORE: Value of x before func10: " + arg + ". Add one to value.");
				arg = arg + 1;
				context.SetArgumentValue(0, arg);
			}
		}

		public void after(JoinPointContext context)
		{
			report("after " + context.StartSelector);
		  
			if (context.StartSelector.Equals("func4"))
			{
				String ret = (String)context.ReturnValue;
				ret = ret + " #After#";
				context.ReturnValue = ret;
			}
			else if (context.StartSelector.Equals("func9"))
			{
				int arg = (int)context.GetArgumentValue(0);
				Console.WriteLine("AFTER: Value of x after func9: " + arg + ". Add one to value.");
				arg = arg + 1;
				context.SetArgumentValue(0, arg);
			}
			else if (context.StartSelector.Equals("func10"))
			{
				int arg = (int)context.GetArgumentValue(0);
				Console.WriteLine("AFTER: Value of x after func10: " + arg + ". Add one to value.");
				arg = arg + 1;
				context.SetArgumentValue(0, arg);
			}
		}

		public void after2(JoinPointContext context)
		{
			report("after2 " + context.StartSelector);

			if (context.StartSelector.Equals("func4"))
			{
				String ret = (String)context.ReturnValue;
				ret = ret + " #After2#";
				context.ReturnValue = ret;
			}
		}
	}
}
