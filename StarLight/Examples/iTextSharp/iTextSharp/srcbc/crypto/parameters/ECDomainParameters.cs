using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;
using org.bouncycastle.math.ec;

namespace org.bouncycastle.crypto.parameters
{
	public class ECDomainParameters
	{
			ECCurve     curve;
			byte[]      seed;
			ECPoint     G;
			BigInteger  n;
			BigInteger  h;

		public ECDomainParameters(
			ECCurve     curve,
			ECPoint     G,
			BigInteger  n)
		{
			this.curve = curve;
			this.G = G;
			this.n = n;
			this.h = ECConstants.ONE;
			this.seed = null;
		}

		public ECDomainParameters(
			ECCurve     curve,
			ECPoint     G,
			BigInteger  n,
			BigInteger  h)
		{
			this.curve = curve;
			this.G = G;
			this.n = n;
			this.h = h;
			this.seed = null;
		}

		public ECDomainParameters(
			ECCurve     curve,
			ECPoint     G,
			BigInteger  n,
			BigInteger  h,
			byte[]      seed)
		{
			this.curve = curve;
			this.G = G;
			this.n = n;
			this.h = h;
			this.seed = seed;
		}

		public ECCurve getCurve()
		{
			return curve;
		}

		public ECPoint getG()
		{
			return G;
		}

		public BigInteger getN()
		{
			return n;
		}

		public BigInteger getH()
		{
			return h;
		}

		public byte[] getSeed()
		{
			return seed;
		}

        public override bool Equals(object obj)
        {
            if (obj is ECDomainParameters)
            {
                if (seed != null && !seed.Equals(((ECDomainParameters)obj).getSeed())) return false;
                if (h != null && !h.Equals(((ECDomainParameters)obj).getH())) return false;
                return (curve.Equals(((ECDomainParameters)obj).getCurve()) && G.Equals(((ECDomainParameters)obj).getG()) && n.Equals(((ECDomainParameters)obj).getN()));
            }

            return base.Equals(obj);
        }

        public override int GetHashCode()
        {
            int code = this.getG().GetHashCode() ^ this.getN().GetHashCode() ^ this.getCurve().GetHashCode();

            if (seed != null)
            {
                code = code ^ this.getSeed().GetHashCode();
            }

            if (h != null)
            {
                code = code ^ this.getH().GetHashCode();
            }

            return code;
        }
    }

}