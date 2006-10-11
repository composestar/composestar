using System;
using org.bouncycastle.security;
using org.bouncycastle.math;
using org.bouncycastle.crypto;


namespace org.bouncycastle.crypto.parameters
{
	public class RSAKeyGenerationParameters: KeyGenerationParameters
	{
		private BigInteger publicExponent;
		private int certainty;

		public RSAKeyGenerationParameters(
			BigInteger		publicExponent,
			SecureRandom    random,
			int             strength,
			int             certainty) : base(random, strength)
		{
			this.publicExponent = publicExponent;
			this.certainty = certainty;
		}

		public BigInteger getPublicExponent()
		{
			return publicExponent;
		}

		public int getCertainty()
		{
			return certainty;
		}
	}
}