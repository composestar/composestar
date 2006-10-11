using System;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class ECPrivateKeyParameters : ECKeyParameters
	{
		BigInteger d;

		public ECPrivateKeyParameters(
			BigInteger          d,
			ECDomainParameters  parameters) : base(true, parameters)
		{
			this.d = d;
		}

		public BigInteger getD()
		{
			return d;
		}


        public override bool Equals(object obj)
        {
            if (obj is ECPrivateKeyParameters)
            {
                return (d.Equals(((ECPrivateKeyParameters)obj).getD()) && this.getParameters().Equals(((ECPrivateKeyParameters)obj).getParameters()));
            }

            return base.Equals(obj);

        }

        public override int GetHashCode()
        {
            return (this.getD().GetHashCode() ^ this.getParameters().GetHashCode());
        }
    }
}