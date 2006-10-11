using System;
using org.bouncycastle.math;
using org.bouncycastle.math.ec;
using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	public class ECKeyPairGenerator : AsymmetricCipherKeyPairGenerator
	{
		ECDomainParameters  parameters;
		SecureRandom        random;

		public void init(
			KeyGenerationParameters param)
		{
			ECKeyGenerationParameters  ecP = (ECKeyGenerationParameters)param;

			this.random = ecP.getRandom();
			this.parameters = ecP.getDomainParameters();
		}

		/**
		 * Given the domain parameters this routine generates an EC key
		 * pair in accordance with X9.62 section 5.2.1 pages 26, 27.
		 */
		public AsymmetricCipherKeyPair generateKeyPair()
		{
			BigInteger n = parameters.getN();

			BigInteger d;

			do
			{
				d = new BigInteger(n.bitLength(), random);
			}
			while (d.Equals(ECConstants.ZERO) || (d.compareTo(n) >= 0));

			ECPoint Q = parameters.getG().multiply(d);

			return new AsymmetricCipherKeyPair(
				new ECPublicKeyParameters(Q, parameters),
				new ECPrivateKeyParameters(d, parameters));
		}
	}

}