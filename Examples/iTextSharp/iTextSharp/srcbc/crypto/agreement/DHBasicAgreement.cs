using System;
using org.bouncycastle.math;
using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.agreement
{
	/**
	 * a Diffie-Hellman key agreement class.
	 * <p>
	 * note: This is only the basic algorithm, it doesn't take advantage of
	 * long term public keys if they are available. See the DHAgreement class
	 * for a "better" implementation.
	 */
	public class DHBasicAgreement: BasicAgreement
	{
		private DHPrivateKeyParameters  key;
		private DHParameters            dhParams;
		private SecureRandom            random;

		public void init(
			CipherParameters    param)
		{
			AsymmetricKeyParameter  kParam;

			if (typeof(ParametersWithRandom).IsInstanceOfType(param))
			{
				ParametersWithRandom    rParam = (ParametersWithRandom)param;

				this.random = rParam.getRandom();
				kParam = (AsymmetricKeyParameter)rParam.getParameters();
			}
			else
			{
				this.random = new SecureRandom();
				kParam = (AsymmetricKeyParameter)param;
			}

			if (!(typeof(DHPrivateKeyParameters).IsInstanceOfType(kParam)))
			{
				throw new ArgumentException("DHEngine expects DHPrivateKeyParameters");
			}

			this.key = (DHPrivateKeyParameters)kParam;
			this.dhParams = key.getParameters();
		}

		/**
		 * given a short term public key from a given party calculate the next
		 * message in the agreement sequence. 
		 */
		public BigInteger calculateAgreement(
			CipherParameters   pubKey)
		{
			DHPublicKeyParameters   pub = (DHPublicKeyParameters)pubKey;

			if (!pub.getParameters().Equals(dhParams))
			{
				throw new ArgumentException("Diffie-Hellman public key has wrong parameters.");
			}

			return pub.getY().modPow(key.getX(), dhParams.getP());
		}
	}

}