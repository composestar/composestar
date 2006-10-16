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
                Int32 arg = (Int32) context.GetArgumentValue(0);
                arg = arg + 1;
                context.AddArgument(0, context.GetArgumentType(0), arg);
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
