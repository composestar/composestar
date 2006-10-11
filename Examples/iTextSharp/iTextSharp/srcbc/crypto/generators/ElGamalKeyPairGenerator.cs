using System;
using org.bouncycastle.math;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	/**
	 * a ElGamal key pair generator.
	 * <p>
	 * This generates keys consistent for use with ElGamal as described in
	 * page 164 of "Handbook of Applied Cryptography".
	 */
	public class ElGamalKeyPairGenerator : AsymmetricCipherKeyPairGenerator
	{
		private ElGamalKeyGenerationParameters param;

		public void init(
					KeyGenerationParameters param)
		{
			this.param = (ElGamalKeyGenerationParameters)param;
		}

		public AsymmetricCipherKeyPair generateKeyPair()
		{
			BigInteger           p, g, x, y;
			int                  qLength = param.getStrength() - 1;
			ElGamalParameters    elParams = param.getParameters();

			p = elParams.getP();
			g = elParams.getG();
    
			//
			// calculate the private key
			//
			x = new BigInteger(qLength, param.getRandom());

			//
			// calculate the public key.
			//
			y = g.modPow(x, p);

			return new AsymmetricCipherKeyPair(
				new ElGamalPublicKeyParameters(y, elParams),
				new ElGamalPrivateKeyParameters(x, elParams));
		}
	}

}