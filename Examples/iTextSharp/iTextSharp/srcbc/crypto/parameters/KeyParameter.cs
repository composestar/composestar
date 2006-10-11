using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	public class KeyParameter : CipherParameters
	{
		private byte[]  key;

		public KeyParameter(byte[]  key) 
			: this(key, 0, key.Length) { }

		public KeyParameter(
			byte[]  key,
			int     keyOff,
			int     keyLen)
		{
			this.key = new byte[keyLen];
			Array.Copy(key, keyOff, this.key, 0, keyLen);
		}

		public byte[] getKey()
		{
			return key;
		}
	}

}