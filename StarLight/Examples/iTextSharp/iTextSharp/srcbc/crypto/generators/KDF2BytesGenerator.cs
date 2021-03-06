using System;
//using org.bouncycastle.math;
//using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	/**
	* Generator for PBE derived keys and ivs as defined by IEEE P1363a
	* <p>
	* This implementation is based on draft 9 of IEEE P1363a. <b>Note:</b>
	* as this is still a draft the output of this generator may change, don't
	* use it for anything that might be subject to long term storage.
	*/
	public class KDF2BytesGenerator : DerivationFunction
	{
		private Digest  digest;
		private byte[]  shared;
		private byte[]  iv;

		/**
		* Construct a KDF2 Parameters generator. Generates key material
		* according to IEEE P1363a - if you want orthodox results you should
		* use a digest specified in the standard.
		* <p>
		* <b>Note:</b> IEEE P1363a standard is still a draft standard, if the standard
		* changes this function, the output of this function will change as well.
		* Don't use this routine for anything subject to long term storage.
		*
		* @param digest the digest to be used as the source of derived keys.
		*/
		public KDF2BytesGenerator(
			Digest  digest)
		{
			this.digest = digest;
		}

		public void init(
			DerivationParameters    param)
		{
			if (!(typeof(KDFParameters).IsInstanceOfType(param)))
			{
				throw new ArgumentException("KDF parameters required for KDF2Generator");
			}

			KDFParameters   p = (KDFParameters)param;

			shared = p.getSharedSecret();
			iv = p.getIV();
		}

		/**
		* return the underlying digest.
		*/
		public Digest getDigest()
		{
			return digest;
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
			if ((outBytes.Length - len) < outOff)
			{
				throw new DataLengthException("output buffer too small");
			}

			long    oBits = len * 8;

			//
			// this is at odds with the standard implementation, the
			// maximum value should be hBits * (2^23 - 1) where hBits
			// is the digest output size in bits. We can't have an
			// array with a long index at the moment...
			//
			if (oBits > (digest.getDigestSize() * 8 * (2L^32 - 1)))
			{
				new ArgumentException("Output length to large");
			}
	    
			int cThreshold = (int)(oBits / digest.getDigestSize());

			byte[] dig = null;

			dig = new byte[digest.getDigestSize()];

			for (int counter = 1; counter <= cThreshold; counter++)
			{
				digest.update(shared, 0, shared.Length);

				digest.update((byte)(counter & 0xff));
				digest.update((byte)((counter >> 8) & 0xff));
				digest.update((byte)((counter >> 16) & 0xff));
				digest.update((byte)((counter >> 24) & 0xff));

				digest.update(iv, 0, iv.Length);

				digest.doFinal(dig, 0);

				if ((len - outOff) > dig.Length)
				{
					Array.Copy(dig, 0, outBytes, outOff, dig.Length);
					outOff += dig.Length;
				}
				else
				{
					Array.Copy(dig, 0, outBytes, outOff, len - outOff);
				}
			}
	    
			digest.reset();

			return len;
		}
	}

}