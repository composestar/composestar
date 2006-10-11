using System;

namespace org.bouncycastle.crypto.digests
{
	/**
	 * Draft FIPS 180-2 implementation of SHA-384. <b>Note:</b> As this is
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
	public class SHA384Digest : LongDigest
	{

		private static readonly int	DIGEST_LENGTH = 48;

		public SHA384Digest()
		{
		}

		/**
		 * Copy constructor.  This will copy the state of the provided
		 * message digest.
		 */
		public SHA384Digest(SHA384Digest t) : base (t) { }

		public override String getAlgorithmName()
		{
			return "SHA-384";
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

			reset();

			return DIGEST_LENGTH;
		}

		/**
		* reset the chaining variables
		*/
		public override void reset()
		{
			base.reset();

			/* SHA-384 initial hash value
				* The first 64 bits of the fractional parts of the square roots
				* of the 9th through 16th prime numbers
				*/
			H1 = unchecked((long) 0xcbbb9d5dc1059ed8L);
			H2 = unchecked((long) 0x629a292a367cd507L);
			H3 = unchecked((long) 0x9159015a3070dd17L);
			H4 = unchecked((long) 0x152fecd8f70e5939L);
			H5 = unchecked((long) 0x67332667ffc00b31L);
			H6 = unchecked((long) 0x8eb44a8768581511L);
			H7 = unchecked((long) 0xdb0c2e0d64f98fa7L);
			H8 = unchecked((long) 0x47b5481dbefa4fa4L);
		}
	}
}