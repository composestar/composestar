using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class RC2Parameters: CipherParameters
	{
		private byte[]  key;
		private int     bits;

		public RC2Parameters(byte[]  key) 
			: this(key, (key.Length > 128) ? 1024 : (key.Length * 8)) {	}

		public RC2Parameters(
			byte[]  key,
			int     bits)
		{
			this.key = new byte[key.Length];
			this.bits = bits;

			Array.Copy(key, 0, this.key, 0, key.Length);
		}

		public byte[] getKey()
		{
			return key;
		}

		public int getEffectiveKeyBits()
		{
			return bits;
		}
	}

}