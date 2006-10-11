using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class DSAPrivateKeyParameters : DSAKeyParameters
	{
		private BigInteger      x;

		public DSAPrivateKeyParameters(
			BigInteger      x,
			DSAParameters   parameters)  : base(true, parameters)
		{
			this.x = x;
		}   

		public BigInteger getX()
		{
			return x;
		}

        public override bool Equals(object obj)
        {
            if (obj is DSAPrivateKeyParameters)
            {
                if (((DSAPrivateKeyParameters)obj).getX().Equals(this.getX()) && ((DSAKeyParameters)obj).getParameters().Equals(this.getParameters()))
                {
                    return true;
                }
            }

            return false;
        }

        public override int GetHashCode()
        {
            return this.getX().GetHashCode() ^ this.getParameters().GetHashCode() ^ (this.isPrivate() ? 1 : 0);
        }
    }

}