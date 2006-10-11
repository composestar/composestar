using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class DSAParameters : CipherParameters
	{
		private BigInteger              g;
		private BigInteger              q;
		private BigInteger              p;
		private DSAValidationParameters validation;

		public DSAParameters(
			BigInteger  p,
			BigInteger  q,
			BigInteger  g)
		{
			this.g = g;
			this.p = p;
			this.q = q;
		}   

		public DSAParameters(
			BigInteger              p,
			BigInteger              q,
			BigInteger              g,
			DSAValidationParameters parameters)
		{
			this.g = g;
			this.p = p;
			this.q = q;
			this.validation = parameters;
		}   

		public BigInteger getP()
		{
			return p;
		}

		public BigInteger getQ()
		{
			return q;
		}

		public BigInteger getG()
		{
			return g;
		}

		public DSAValidationParameters getValidationParameters()
		{
			return validation;
		}

        public override bool Equals(object obj)
        {
            if (obj is DSAParameters)
            {
                DSAParameters pm = (DSAParameters)obj;

                return (pm.getP().Equals(p) && pm.getQ().Equals(q) && pm.getG().Equals(g));
            }

            return false;
        }

        public override int GetHashCode()
        {
            return this.getP().GetHashCode() ^ this.getQ().GetHashCode() ^ this.getG().GetHashCode();
        }
    }
}