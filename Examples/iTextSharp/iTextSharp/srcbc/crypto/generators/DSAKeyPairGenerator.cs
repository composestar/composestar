using System;
using org.bouncycastle.math;
using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	/**
	 * a DSA key pair generator.
	 *
	 * This generates DSA keys in line with the method described 
	 * in FIPS 186-2.
	 */
	public class DSAKeyPairGenerator : AsymmetricCipherKeyPairGenerator
	{
		private static BigInteger ZERO = BigInteger.valueOf(0);

		private DSAKeyGenerationParameters param;

		public void init(
			KeyGenerationParameters param)
		{
			this.param = (DSAKeyGenerationParameters)param;
		}

		public AsymmetricCipherKeyPair generateKeyPair()
		{
			BigInteger      p, q, g, x, y;
			DSAParameters   dsaParams = param.getParameters();
			SecureRandom    random = param.getRandom();

			q = dsaParams.getQ();
			p = dsaParams.getP();
			g = dsaParams.getG();

			do
			{
				x = new BigInteger(160, random);
			}
			while (x.Equals(ZERO) || x.compareTo(q) >= 0);

			//
			// calculate the public key.
			//
			y = g.modPow(x, p);

			return new AsymmetricCipherKeyPair(
				new DSAPublicKeyParameters(y, dsaParams),
				new DSAPrivateKeyParameters(x, dsaParams));
		}
	}

}