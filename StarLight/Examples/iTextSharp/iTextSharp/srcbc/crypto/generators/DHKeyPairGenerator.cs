using System;
using org.bouncycastle.math;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	/**
	 * a Diffie-Helman key pair generator.
	 *
	 * This generates keys consistent for use in the MTI/A0 key agreement protocol
	 * as described in "Handbook of Applied Cryptography", Pages 516-519.
	 */
	public class DHKeyPairGenerator : AsymmetricCipherKeyPairGenerator
	{
		private DHKeyGenerationParameters param;

		public virtual void init(KeyGenerationParameters param)
		{
			this.param = (DHKeyGenerationParameters)param;
		}

		public virtual AsymmetricCipherKeyPair generateKeyPair()
		{
			BigInteger      p, g, x, y;
			int             qLength = param.getStrength() - 1;
			DHParameters    dhParams = param.getParameters();

			p = dhParams.getP();
			g = dhParams.getG();
    
			//
			// calculate the private key
			//
			x = new BigInteger(qLength, param.getRandom());

			//
			// calculate the public key.
			//
			y = g.modPow(x, p);

			return new AsymmetricCipherKeyPair(
				new DHPublicKeyParameters(y, dhParams),
				new DHPrivateKeyParameters(x, dhParams));
		}
	}

}