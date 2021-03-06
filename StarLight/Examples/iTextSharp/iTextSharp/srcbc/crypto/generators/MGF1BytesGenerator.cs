using System;
//using org.bouncycastle.math;
//using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	/**
	* Generator for MGF1 as defined in PKCS 1v2
	*/
	public class MGF1BytesGenerator : DerivationFunction
	{
		private Digest  digest;
		private byte[]  seed;
		private int     hLen;

		/**
		* @param digest the digest to be used as the source of generated bytes
		*/
		public MGF1BytesGenerator(
			Digest  digest)
		{
			this.digest = digest;
			this.hLen = digest.getDigestSize();
		}

		public void init(
			DerivationParameters    param)
		{
			if (!(typeof(MGFParameters).IsInstanceOfType(param)))
			{
				throw new ArgumentException("MGF parameters required for MGF1Generator");
			}

			MGFParameters   p = (MGFParameters)param;

			seed = p.getSeed();
		}

		/**
		* return the underlying digest.
		*/
		public Digest getDigest()
		{
			return digest;
		}

		/**
		* int to octet string.
		*/
		private void ItoOSP(
			int     i,
			byte[]  sp)
		{
			sp[0] = (byte)((uint) i >> 24);
			sp[1] = (byte)((uint) i >> 16);
			sp[2] = (byte)((uint) i >> 8);
			sp[3] = (byte)((uint) i >> 0);
		}

		/**
		* fill len bytes of the output buffer with bytes generated from
		* the derivation function.
		*
		* @throws ArgumentException if the size of the request will cause an overflow.
		* @throws DataLengthException if the out buffer is too small.
		*/
		public int generateBytes(
			byte[]  outBytes,
			int     outOff,
			int     len)
			//throws DataLengthException, ArgumentException
		{
			byte[]  hashBuf = new byte[hLen];
			byte[]  C = new byte[4];
			int     counter = 0;

			digest.reset();

			do
			{
				ItoOSP(counter, C);

				digest.update(seed, 0, seed.Length);
				digest.update(C, 0, C.Length);
				digest.doFinal(hashBuf, 0);

				Array.Copy(hashBuf, 0, outBytes, outOff + counter * hLen, hLen);
			}
			while (++counter < (len / hLen));

			if ((counter * hLen) < len)
			{
				ItoOSP(counter, C);

				digest.update(seed, 0, seed.Length);
				digest.update(C, 0, C.Length);
				digest.doFinal(hashBuf, 0);

				Array.Copy(hashBuf, 0, outBytes, outOff + counter * hLen, len - (counter * hLen));
			}

			return len;
		}
	}

}