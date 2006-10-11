using System;
using org.bouncycastle.math;
using org.bouncycastle.math.ec;

namespace org.bouncycastle.crypto.parameters
{
	public class ECPublicKeyParameters : ECKeyParameters
	{
		ECPoint Q;

		public ECPublicKeyParameters(
			ECPoint             Q,
			ECDomainParameters  parameters) : base(false, parameters)
		{
			this.Q = Q;
		}

		public ECPoint getQ()
		{
			return Q;
		}


        public override bool Equals(object obj)
        {
            if (obj is ECPublicKeyParameters)
            {
                return (((ECPublicKeyParameters)obj).getQ().Equals(this.getQ()) && ((ECPublicKeyParameters)obj).getParameters().Equals(this.getParameters()));
            }
            return base.Equals(obj);
        }

        public override int GetHashCode()
        {
            return (this.getQ().GetHashCode() ^ this.getParameters().GetHashCode());
        }

    }

}