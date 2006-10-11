using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class DHPublicKeyParameters : DHKeyParameters
	{
		private BigInteger      y;

		public DHPublicKeyParameters(
			BigInteger      y,
			DHParameters    parameters) : base(false, parameters)
		{
			this.y = y;
		}   

		public BigInteger getY()
		{
			return y;
		}

		public override bool Equals(Object  obj)
		{
            if (obj is DHPublicKeyParameters)
            {
                return (((DHPublicKeyParameters)obj).getY().Equals(y) && ((DHPublicKeyParameters)obj).getParameters().Equals(this.getParameters()));
            }

            return base.Equals(obj);
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	}

}