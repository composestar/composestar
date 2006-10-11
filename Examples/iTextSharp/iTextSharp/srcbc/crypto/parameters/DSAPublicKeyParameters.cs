using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class DSAPublicKeyParameters : DSAKeyParameters
	{
		private BigInteger      y;

		public DSAPublicKeyParameters(
			BigInteger      y,
			DSAParameters   parameters) : base(false, parameters)
		{
			this.y = y;
		}   

		public BigInteger getY()
		{
			return y;
		}

        public override bool Equals(object obj)
        {
            if (obj is DSAPublicKeyParameters)
            {
                if (((DSAPublicKeyParameters)obj).getY().Equals(this.getY()) && ((DSAKeyParameters)obj).getParameters().Equals(this.getParameters()))
                {
                    return true;
                }
            }

            return false;
        }

        public override int GetHashCode()
        {
            return this.getY().GetHashCode() ^ this.getParameters().GetHashCode() ^ (this.isPrivate() ? 1 : 0);
        }
    }

}