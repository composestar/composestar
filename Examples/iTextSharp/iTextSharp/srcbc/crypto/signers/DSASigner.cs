using System;
using org.bouncycastle.math;
using org.bouncycastle.math.ec;
using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.digests;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.signers
{
	/**
	 * The Digital Signature Algorithm - as described in "Handbook of Applied
	 * Cryptography", pages 452 - 453.
	 */
	public class DSASigner: DSA
	{
		DSAKeyParameters key;

		SecureRandom    random;

		public void init(
			bool                 forSigning,
			CipherParameters        param)
		{
			if (forSigning)
			{
				if (typeof(ParametersWithRandom).IsInstanceOfType(param))
				{
					ParametersWithRandom    rParam = (ParametersWithRandom)param;

					this.random = rParam.getRandom();
					this.key = (DSAPrivateKeyParameters)rParam.getParameters();
				}
			else
			{
				this.random = new SecureRandom();
				this.key = (DSAPrivateKeyParameters)param;
			}
			}
			else
			{
				this.key = (DSAPublicKeyParameters)param;
			}
		}

		/**
		 * generate a signature for the given message using the key we were
		 * initialised with. For conventional DSA the message should be a SHA-1
		 * hash of the message of interest.
		 *
		 * @param message the message that will be verified later.
		 */
		public BigInteger[] generateSignature(
			byte[] message)
		{
			BigInteger      m = new BigInteger(1, message);
			DSAParameters   parameters = key.getParameters();
			BigInteger      k;

			do 
			{
				k = new BigInteger(parameters.getQ().bitLength(), random);
			}
			while (k.compareTo(parameters.getQ()) >= 0);

			BigInteger  r = parameters.getG().modPow(k, parameters.getP()).mod(parameters.getQ());

			k = k.modInverse(parameters.getQ()).multiply(
				m.add(((DSAPrivateKeyParameters)key).getX().multiply(r)));

			BigInteger  s = k.mod(parameters.getQ());

			BigInteger[]  res = new BigInteger[2];

			res[0] = r;
			res[1] = s;

			return res;
		}

		/**
		 * return true if the value r and s represent a DSA signature for
		 * the passed in message for standard DSA the message should be a
		 * SHA-1 hash of the real message to be verified.
		 */
		public bool verifySignature(
			byte[]      message,
			BigInteger  r,
			BigInteger  s)
		{
			BigInteger      m = new BigInteger(1, message);
			DSAParameters   parameters = key.getParameters();
			BigInteger      zero = BigInteger.valueOf(0);

			if (zero.compareTo(r) >= 0 || parameters.getQ().compareTo(r) <= 0)
			{
				return false;
			}

			if (zero.compareTo(s) >= 0 || parameters.getQ().compareTo(s) <= 0)
			{
				return false;
			}

			BigInteger  w = s.modInverse(parameters.getQ());

			BigInteger  u1 = m.multiply(w).mod(parameters.getQ());
			BigInteger  u2 = r.multiply(w).mod(parameters.getQ());

			u1 = parameters.getG().modPow(u1, parameters.getP());
			u2 = ((DSAPublicKeyParameters)key).getY().modPow(u2, parameters.getP());

			BigInteger  v = u1.multiply(u2).mod(parameters.getP()).mod(parameters.getQ());

			return v.Equals(r);
		}
	}

}