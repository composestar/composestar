using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class ElGamalPublicKeyParameters : ElGamalKeyParameters
	{
		private BigInteger      y;

		public ElGamalPublicKeyParameters(
			BigInteger      y,
			ElGamalParameters    parameters) : base(false, parameters)
		{
			this.y = y;
		}   

		public BigInteger getY()
		{
			return y;
		}

		public override bool Equals(
			Object  obj)
		{

            if (obj is ElGamalPublicKeyParameters)
            {
                return (((ElGamalPublicKeyParameters)obj).getY().Equals(y) &&
                    ((ElGamalPublicKeyParameters)obj).getParameters().getG().Equals(this.getParameters().getG()) &&
                    ((ElGamalPublicKeyParameters)obj).getParameters().getP().Equals(this.getParameters().getP()));
            }

            return base.Equals(obj);
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	
      
    
    }

}