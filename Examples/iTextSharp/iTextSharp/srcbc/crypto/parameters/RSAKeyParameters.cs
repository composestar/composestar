using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class RSAKeyParameters : AsymmetricKeyParameter
	{
		private BigInteger      modulus;
		private BigInteger      exponent;

		public RSAKeyParameters(
			bool     isPrivate,
			BigInteger  modulus,
			BigInteger  exponent) : base(isPrivate)
		{
			this.modulus = modulus;
			this.exponent = exponent;
		}   

		public BigInteger getModulus()
		{
			return modulus;
		}

		public BigInteger getExponent()
		{
			return exponent;
		}

        public override bool Equals(object obj)
        {
            if (obj is RSAKeyParameters)
            {
                RSAKeyParameters   kp = (RSAKeyParameters)obj;

                if (kp.isPrivate() == this.isPrivate()
                    && kp.getModulus().Equals(this.modulus)
                    && kp.getExponent().Equals(this.exponent))
                {
                    return true;
                }
            }

            return false;
        }

        public override int GetHashCode()
        {
            return this.getModulus().GetHashCode() ^ this.getExponent().GetHashCode() ^ (this.isPrivate() ? 1 : 0);
        }
    }

}
