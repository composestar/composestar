using System;
using Composestar.RuntimeCore.FLIRT.Message;
using Composestar.Reasoning;

		/**
		 *   MAPPING message functions to resource operations
		 * 
		 *   message.resume() -> message.fork
		 *   message.proceed() -> message.proceed
		 *   message.respond() -> message.Respond
		 *   message.reply() -> message.return
		 * 
		 * 
		 *  Just for demonstrating purposes
		 * 
		 **/


namespace SECRETTesting
{
	public class CryptACT
	{
		[Semantics("args.read,args.write,message.reply")]
		public void encrypt(ReifiedMessage message)
		{
		}
	}
}