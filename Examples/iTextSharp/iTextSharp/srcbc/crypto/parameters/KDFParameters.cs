using System;
using org.bouncycastle.crypto;

namespace org.bouncycastle.crypto.parameters
{
	/**
	 * parameters for Key derivation functions.
	 */
	public class KDFParameters : DerivationParameters
	{
		byte[]  iv;
		byte[]  shared;

		public KDFParameters(
			byte[]  shared,
			byte[]  iv)
		{
			this.shared = shared;
			this.iv = iv;
		}

		public byte[] getSharedSecret()
		{
			return shared;
		}

		public byte[] getIV()
		{
			return iv;
		}
	}

}