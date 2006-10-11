using System;

namespace org.bouncycastle.crypto.parameters
{
	public class DESedeParameters : DESParameters
	{
		/*
		* DES-EDE Key length in bytes.
		*/
		static public readonly int DES_EDE_KEY_LENGTH = 24;

		public DESedeParameters(
			byte[]  key) : base(key)
		{
			if (isWeakKey(key, 0, 0))
			{
				throw new ArgumentException("attempt to create weak DESede key");
			}
		}

		/**
		 * return true if the passed in key is a DES-EDE weak key.
		 *
		 * @param key bytes making up the key
		 * @param offset offset into the byte array the key starts at
		 * @param length number of bytes making up the key
		 */
		public static bool isWeakKey(
			byte[]  key,
			int     offset,
			int     length)
		{
			for (int i = offset; i < length; i += DES_KEY_LENGTH)
			{
				if (DESParameters.isWeakKey(key, i))
				{
					return true;
				}
			}

			return false;
		}

		/**
		 * return true if the passed in key is a DES-EDE weak key.
		 *
		 * @param key bytes making up the key
		 * @param offset offset into the byte array the key starts at
		 */
		public static new bool isWeakKey(
			byte[]  key,
			int     offset)
		{
			return DESedeParameters.isWeakKey(key, offset, key.Length - offset);
		}
	}

}