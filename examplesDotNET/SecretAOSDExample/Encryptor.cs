using System;
using Composestar.RuntimeCore.FLIRT.Message;
using Composestar.RuntimeDotNET;

namespace SecretAOSDExample
{
	public class Encryptor
	{
		[@Semantics("args.Encrypt")]
		public void encrypt(ReifiedMessage rm)
		{
			string msg = (string)rm.getArg(0);
			char[] tmp = new char[msg.Length];
			char[] tmp1 = msg.ToCharArray();
			for(int i=0; i<tmp1.Length; i++)
			{
				tmp[i] = (char)(tmp1[i]+20);
			}
			Console.WriteLine("Encrypting: "+msg+" to: "+new String(tmp));
			rm.setArg(0,new String(tmp));
			rm.proceed();
		}

		public string textEncrypt(string msg)
		{
			char[] tmp = new char[msg.Length];
			char[] tmp1 = msg.ToCharArray();
			for(int i=0; i<tmp1.Length; i++)
			{
				tmp[i] = (char)(tmp1[i]+20);
			}
			//Console.WriteLine("Encrypting: "+msg+" to: "+new String(tmp));
			return new String(tmp);
		}

		[@Semantics("args.decrypt")]
		public void decrypt(ReifiedMessage rm)
		{
			string msg = (string)rm.getArg(0);
			char[] tmp = new char[msg.Length];
			char[] tmp1 = msg.ToCharArray();
			for(int i=0; i<tmp1.Length; i++)
			{
				tmp[i] = (char)(tmp1[i]-20);
			}
			Console.WriteLine("Decrypting: "+msg+" to: "+new String(tmp));
			rm.setArg(0,new String(tmp));
			rm.proceed();
		}
	}
}

