using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class RC5Parameters : CipherParameters
	{
		private byte[]  key;
		private int     rounds;

		public RC5Parameters(
			byte[]  key,
			int     rounds)
		{
			if (key.Length > 255)
			{
				throw new ArgumentException("RC5 key length can be no greater than 255");
			}

			this.key = new byte[key.Length];
			this.rounds = rounds;

			Array.Copy(key, 0, this.key, 0, key.Length);
		}

		public byte[] getKey()
		{
			return key;
		}

		public int getRounds()
		{
			return rounds;
		}
	}

}