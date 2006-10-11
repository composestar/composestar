using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	/**
	 * parameters for mask derivation functions.
	 */
	public class MGFParameters : DerivationParameters
	{
		byte[]  seed;

		public MGFParameters(
			byte[]  seed)
		{
			this.seed = seed;
		}

		public MGFParameters(
			byte[]  seed,
			int     off,
			int     len)
		{
			this.seed = new byte[len];
			Array.Copy(seed, off, this.seed, 0, len);
		}

		public byte[] getSeed()
		{
			return seed;
		}
	}

}