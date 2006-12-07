using System;
using Composestar.RuntimeCore.FLIRT.Message;
using Composestar.RuntimeDotNET;

namespace EncryptionExample
{
	public class Logger
	{
		public Logger() { }

		[Composestar.RuntimeDotNET.Semantics("args.read")]
		public void log(ReifiedMessage rm)
		{
			string tmp = (string)rm.getArg(0);
			Console.WriteLine("Logging: ["+tmp+"]");
			rm.proceed();
		}

		public static void slog(String msg)
		{
			Console.WriteLine("Logging: ["+msg+"]");
		}
	}
}