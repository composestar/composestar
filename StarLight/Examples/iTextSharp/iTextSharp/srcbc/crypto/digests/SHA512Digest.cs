using System;

namespace org.bouncycastle.crypto.digests
{
	/**
	 * Draft FIPS 180-2 implementation of SHA-512. <b>Note:</b> As this is
	 * based on a draft this implementation is subject to change.
	 *
	 * <pre>
	 *         block  word  digest
	 * SHA-1   512    32    160
	 * SHA-256 512    32    256
	 * SHA-384 1024   64    384
	 * SHA-512 1024   64    512
	 * </pre>
	 */

	public class SHA512Digest : LongDigest
	{

		private static readonly int	DIGEST_LENGTH = 64;

		public SHA512Digest()
		{
		}

		/**
		 * Copy constructor.  This will copy the state of the provided
		 * message digest.
		 */
		public SHA512Digest(SHA512Digest t) : base (t) { }

		public override String getAlgorithmName()
		{
			return "SHA-512";
		}

		public override int getDigestSize()
		{
			return DIGEST_LENGTH;
		}

		public override int doFinal(
			byte[]  outBytes,
			int     outOff)
		{
			finish();

			unpackWord(H1, outBytes, outOff);
			unpackWord(H2, outBytes, outOff + 8);
			unpackWord(H3, outBytes, outOff + 16);
			unpackWord(H4, outBytes, outOff + 24);
			unpackWord(H5, outBytes, outOff + 32);
			unpackWord(H6, outBytes, outOff + 40);
			unpackWord(H7, outBytes, outOff + 48);
			unpackWord(H8, outBytes, outOff + 56);

			reset();

			return DIGEST_LENGTH;

		}

		/**
		* reset the chaining variables
		*/
		public override void reset()
		{
			base.reset();

			/* SHA-512 initial hash value
			 * The first 64 bits of the fractional parts of the square roots
			 * of the first eight prime numbers
			 */
			H1 = unchecked((long) 0x6a09e667f3bcc908L);
			H2 = unchecked((long) 0xbb67ae8584caa73bL);
			H3 = unchecked((long) 0x3c6ef372fe94f82bL);
			H4 = unchecked((long) 0xa54ff53a5f1d36f1L);
			H5 = unchecked((long) 0x510e527fade682d1L);
			H6 = unchecked((long) 0x9b05688c2b3e6c1fL);
			H7 = unchecked((long) 0x1f83d9abfb41bd6bL);
			H8 = unchecked((long) 0x5be0cd19137e2179L);
		}
	}
}