using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{

	public class ElGamalPrivateKeyParameters : ElGamalKeyParameters
	{
		private BigInteger      x;

		public ElGamalPrivateKeyParameters(
			BigInteger      x,
			ElGamalParameters    parameters) : base(true, parameters)
		{
			this.x = x;
		}   

		public BigInteger getX()
		{
			return x;
		}

		public override bool Equals(
			Object  obj)
		{

            if (obj is ElGamalPrivateKeyParameters)
            {
                if (((ElGamalPrivateKeyParameters)obj).getX().Equals(x) && 
                    ((ElGamalPrivateKeyParameters)obj).getParameters().getG().Equals(this.getParameters().getG()) &&
                    ((ElGamalPrivateKeyParameters)obj).getParameters().getP().Equals(this.getParameters().getP())) { return true; }
            }

    		return base.Equals(obj);
		}

		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	}

}