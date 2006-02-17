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
		 *  Adds a crc-checksum to the argument of the message. Therefor
		 *  the argument cannot be written anymore because then the receiver
		 *  won't be able to check the argument with it's checksum...
		 * 
		 *  Just for demonstrating purposes
		 * 
		 **/


namespace SECRETTesting
{
	public class CrcACT
	{
		[Semantics("message.proceed,args.read,args.write,args.lock,message.return")]
		public void checksum(ReifiedMessage message)
		{
		}
	}
}