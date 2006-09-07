using System;
using Composestar.RuntimeCore.FLIRT.Message;
using Composestar.RuntimeCore.FLIRT.Reflection;
using java.util;
using java.lang;

namespace JPInfo
{
	public class JoinPointInfoExtractor
	{
		public void extract(ReifiedMessage rm)
		{
			Console.WriteLine("\n\nHello for : "+JoinPointInfo.getInternals());
			Enumeration enu = JoinPointInfo.getInternals();
			while(enu.hasMoreElements())
			{
				Console.WriteLine("Internal: "+enu.nextElement().ToString());
			}
			enu = JoinPointInfo.getExternals();
			while(enu.hasMoreElements())
			{
				Console.WriteLine("External: "+enu.nextElement().ToString());
			}
            Console.WriteLine("the receiving instance is a "+JoinPointInfo.getInstance());
			Console.WriteLine("\n\n");
		}

		public bool doCondition()
		{
			Message m = MessageInfo.getMessageInfo();
			Console.WriteLine("doCondition() Message: " + m.getTarget() + "::" + m.getSelector());
			Console.WriteLine("\n\nHallo for : "+JoinPointInfo.getInternals());
			Enumeration enu = JoinPointInfo.getInternals();
			while(enu.hasMoreElements())
			{
				Console.WriteLine("Internal: "+enu.nextElement().ToString());
			}
			enu = JoinPointInfo.getExternals();
			while(enu.hasMoreElements())
			{
				Console.WriteLine("External: "+enu.nextElement().ToString());
			}
			Console.WriteLine("\n\n");
			return true;
		}
	}
}