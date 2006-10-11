using System;

namespace org.bouncycastle.crypto.parameters
{
	public class DESParameters : KeyParameter
	{
		public DESParameters(
			byte[]  key) : base(key)
		{
			if (isWeakKey(key, 0)) throw new ArgumentException("attempt to create weak DES key");
		}

		/*
		* DES Key Length in bytes.
		*/
		static public readonly int DES_KEY_LENGTH = 8;

		/*
		* Table of weak and semi-weak keys taken from Schneier pp281
		*/
		static private readonly int N_DES_WEAK_KEYS = 16;

		static private byte[] DES_weak_keys =
		{
			/* weak keys */
			(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01, (byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,
			(byte)0x1f,(byte)0x1f,(byte)0x1f,(byte)0x1f, (byte)0x0e,(byte)0x0e,(byte)0x0e,(byte)0x0e,
			(byte)0xe0,(byte)0xe0,(byte)0xe0,(byte)0xe0, (byte)0xf1,(byte)0xf1,(byte)0xf1,(byte)0xf1,
			(byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe, (byte)0xfe,(byte)0xfe,(byte)0xfe,(byte)0xfe,

			/* semi-weak keys */
			(byte)0x01,(byte)0xfe,(byte)0x01,(byte)0xfe, (byte)0x01,(byte)0xfe,(byte)0x01,(byte)0xfe,
			(byte)0x1f,(byte)0xe0,(byte)0x1f,(byte)0xe0, (byte)0x0e,(byte)0xf1,(byte)0x0e,(byte)0xf1,
			(byte)0x01,(byte)0xe0,(byte)0x01,(byte)0xe0, (byte)0x01,(byte)0xf1,(byte)0x01,(byte)0xf1,
			(byte)0x1f,(byte)0xfe,(byte)0x1f,(byte)0xfe, (byte)0x0e,(byte)0xfe,(byte)0x0e,(byte)0xfe,
			(byte)0x01,(byte)0x1f,(byte)0x01,(byte)0x1f, (byte)0x01,(byte)0x0e,(byte)0x01,(byte)0x0e,
			(byte)0xe0,(byte)0xfe,(byte)0xe0,(byte)0xfe, (byte)0xf1,(byte)0xfe,(byte)0xf1,(byte)0xfe,
			(byte)0xfe,(byte)0x01,(byte)0xfe,(byte)0x01, (byte)0xfe,(byte)0x01,(byte)0xfe,(byte)0x01,
			(byte)0xe0,(byte)0x1f,(byte)0xe0,(byte)0x1f, (byte)0xf1,(byte)0x0e,(byte)0xf1,(byte)0x0e,
			(byte)0xe0,(byte)0x01,(byte)0xe0,(byte)0x01, (byte)0xf1,(byte)0x01,(byte)0xf1,(byte)0x01,
			(byte)0xfe,(byte)0x1f,(byte)0xfe,(byte)0x1f, (byte)0xfe,(byte)0x0e,(byte)0xfe,(byte)0x0e,
			(byte)0x1f,(byte)0x01,(byte)0x1f,(byte)0x01, (byte)0x0e,(byte)0x01,(byte)0x0e,(byte)0x01,
			(byte)0xfe,(byte)0xe0,(byte)0xfe,(byte)0xe0, (byte)0xfe,(byte)0xf1,(byte)0xfe,(byte)0xf1
		};

		/**
		* DES has 16 weak keys.  This method will check
		* if the given DES key material is weak or semi-weak.
		* Key material that is too short is regarded as weak.
		* <p>
		* See <a href="http://www.counterpane.com/applied.html">"Applied
		* Cryptography"</a> by Bruce Schneier for more information.
		*
		* @return true if the given DES key material is weak or semi-weak,
		*     false otherwise.
		*/
		public static bool isWeakKey(
			byte[] key,
			int offset)
		{
			if (key.Length - offset < DES_KEY_LENGTH)
			{
				throw new ArgumentException("key material too short.");
			}

			//nextkey: 
			for (int i = 0; i < N_DES_WEAK_KEYS; i++)
			{
			    bool unmatch = false;
				for (int j = 0; j < DES_KEY_LENGTH; j++)
				{
					if (key[j + offset] != DES_weak_keys[i * DES_KEY_LENGTH + j])
					{
						//continue nextkey;
						unmatch = true;
					}
				}
				if (unmatch)
					continue;
				else
					return true;
			}
			return false;
		}

		/**
		* DES Keys use the LSB as the odd parity bit.  This can
		* be used to check for corrupt keys.
		*
		* @param bytes the byte array to set the parity on.
		*/
		public static void setOddParity(
			byte[] bytes)
		{
			for (int i = 0; i < bytes.Length; i++)
			{
				int b = bytes[i];
				bytes[i] = (byte)((b & 0xfe) |
								((((b >> 1) ^
								(b >> 2) ^
								(b >> 3) ^
								(b >> 4) ^
								(b >> 5) ^
								(b >> 6) ^
								(b >> 7)) ^ 0x01) & 0x01));
			}
		}
	}

}