using System;
using org.bouncycastle.math;
using org.bouncycastle.security;
using org.bouncycastle.crypto;
using org.bouncycastle.crypto.parameters;

namespace org.bouncycastle.crypto.generators
{
	public class DHParametersGenerator
	{
		private int             size;
		private int             certainty;
		private SecureRandom    random;

		private static BigInteger ONE = BigInteger.valueOf(1);
		private static BigInteger TWO = BigInteger.valueOf(2);

		public virtual void init(
			int             size,
			int             certainty,
			SecureRandom    random)
		{
			this.size = size;
			this.certainty = certainty;
			this.random = random;
		}

		/**
		 * which generates the p and g values from the given parameters,
		 * returning the DHParameters object.
		 * <p>
		 * Note: can take a while...
		 */
		public virtual DHParameters generateParameters()
		{
			BigInteger      g, p, q;
			int             qLength = size - 1;

			//
			// find a safe prime p where p = 2*q + 1, where p and q are prime.
			//
			for (;;)
			{
				q = new BigInteger(qLength, certainty, random);
				p = q.multiply(TWO).add(ONE);
				if (p.isProbablePrime(certainty))
				{
					break;
				}
			}

			//
			// calculate the generator g - the advantage of using the 2q+1 
			// approach is that we know the prime factorisation of (p - 1)...
			//
			for (;;)
			{
				g = new BigInteger(qLength, random);

				if (g.modPow(TWO, p).Equals(ONE))
				{
					continue;
				}

				if (g.modPow(q, p).Equals(ONE))
				{
					continue;
				}

				break;
			}

			return new DHParameters(p, g, q, 2);
		}
	}

}