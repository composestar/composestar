using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{

	public class DHPrivateKeyParameters : DHKeyParameters
	{
		private BigInteger      x;

		public DHPrivateKeyParameters(
			BigInteger      x,
			DHParameters    parameters) : base(true, parameters)
		{	
			this.x = x;
		}   

		public BigInteger getX()
		{
			return x;
		}

		public override bool Equals(Object  obj)
		{
            if (obj is DHPrivateKeyParameters)
            {
                return (((DHPrivateKeyParameters)obj).getX().Equals(x) &&
                        ((DHPrivateKeyParameters)obj).getParameters().Equals(this.getParameters()));
            }
            return base.Equals(obj);
		}

		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	}

}