using System;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.macs
{
	/**
	* HMAC implementation based on RFC2104
	*
	* H(K XOR opad, H(K XOR ipad, text))
	*/
	public class HMac : Mac
	{
		private readonly static int BLOCK_LENGTH = 64;

		private readonly static byte IPAD = (byte)0x36;
		private readonly static byte OPAD = (byte)0x5C;

		private Digest digest;
		private int digestSize;
		private byte[] inputPad = new byte[BLOCK_LENGTH];
		private byte[] outputPad = new byte[BLOCK_LENGTH];

		public HMac(
			Digest digest)
		{
			this.digest = digest;
			digestSize = digest.getDigestSize();
		}

		public String getAlgorithmName()
		{
			return digest.getAlgorithmName() + "/HMAC";
		}

		public Digest getUnderlyingDigest()
		{
			return digest;
		}

		public void init(
			CipherParameters parameters)
		{
			digest.reset();

			byte[] key = ((KeyParameter)parameters).getKey();

			if (key.Length > BLOCK_LENGTH)
			{
				digest.update(key, 0, key.Length);
				digest.doFinal(inputPad, 0);
				for (int i = digestSize; i < inputPad.Length; i++)
				{
					inputPad[i] = 0;
				}
			}
			else
			{
				Array.Copy(key, 0, inputPad, 0, key.Length);
				for (int i = key.Length; i < inputPad.Length; i++)
				{
					inputPad[i] = 0;
				}
			}

			outputPad = new byte[inputPad.Length];
			Array.Copy(inputPad, 0, outputPad, 0, inputPad.Length);

			for (int i = 0; i < inputPad.Length; i++)
			{
				inputPad[i] ^= IPAD;
			}

			for (int i = 0; i < outputPad.Length; i++)
			{
				outputPad[i] ^= OPAD;
			}

			digest.update(inputPad, 0, inputPad.Length);
		}

		public int getMacSize()
		{
			return digestSize;
		}

		public void update(
			byte inByte)
		{
			digest.update(inByte);
		}

		public void update(
			byte[] inBytes,
			int inOff,
			int len)
		{
			digest.update(inBytes, inOff, len);
		}

		public int doFinal(
			byte[] outBytes,
			int outOff)
		{
			byte[] tmp = new byte[digestSize];
			digest.doFinal(tmp, 0);

			digest.update(outputPad, 0, outputPad.Length);
			digest.update(tmp, 0, tmp.Length);

			int     len = digest.doFinal(outBytes, outOff);

			reset();

			return len;
		}

		/**
		* Reset the mac generator.
		*/
		public void reset()
		{
			/*
			* reset the underlying digest.
			*/
			digest.reset();

			/*
			* reinitialize the digest.
			*/
			digest.update(inputPad, 0, inputPad.Length);
		}
	}

}