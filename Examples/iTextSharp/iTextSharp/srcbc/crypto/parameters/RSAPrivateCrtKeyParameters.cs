using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class RSAPrivateCrtKeyParameters : RSAKeyParameters
	{
		private BigInteger  e;
		private BigInteger  p;
		private BigInteger  q;
		private BigInteger  dP;
		private BigInteger  dQ;
		private BigInteger  qInv;

		/**
		 * 
		 */
		public RSAPrivateCrtKeyParameters(
			BigInteger  modulus,
			BigInteger  publicExponent,
			BigInteger  privateExponent,
			BigInteger  p,
			BigInteger  q,
			BigInteger  dP,
			BigInteger  dQ,
			BigInteger  qInv) : base(true, modulus, privateExponent)
		{
			this.e = publicExponent;
			this.p = p;
			this.q = q;
			this.dP = dP;
			this.dQ = dQ;
			this.qInv = qInv;
		}

		public BigInteger getPublicExponent()
		{
			return e;
		}

		public BigInteger getP()
		{
			return p;
		}

		public BigInteger getQ()
		{
			return q;
		}

		public BigInteger getDP()
		{
			return dP;
		}

		public BigInteger getDQ()
		{
			return dQ;
		}

		public BigInteger getQInv()
		{
			return qInv;
		}


        public override bool Equals(object obj)
        {
            if (((RSAPrivateCrtKeyParameters)obj).getDP().Equals(dP) &&
                ((RSAPrivateCrtKeyParameters)obj).getDQ().Equals(dQ) &&
                ((RSAPrivateCrtKeyParameters)obj).getExponent().Equals(this.getExponent()) &&
                ((RSAPrivateCrtKeyParameters)obj).getModulus().Equals(this.getModulus()) &&
                ((RSAPrivateCrtKeyParameters)obj).getP().Equals(p) &&
                ((RSAPrivateCrtKeyParameters)obj).getQ().Equals(q) &&
                ((RSAPrivateCrtKeyParameters)obj).getPublicExponent().Equals(e) &&
                ((RSAPrivateCrtKeyParameters)obj).getQInv().Equals(qInv)) return true;
            return false;
        }

        public override int GetHashCode()
        {
            return (this.getDP().GetHashCode() ^
                    this.getDQ().GetHashCode() ^
                    this.getExponent().GetHashCode() ^
                    this.getModulus().GetHashCode() ^
                    this.getP().GetHashCode() ^
                    this.getQ().GetHashCode() ^
                    this.getPublicExponent().GetHashCode() ^
                    this.getQInv().GetHashCode());
        }
    }

}