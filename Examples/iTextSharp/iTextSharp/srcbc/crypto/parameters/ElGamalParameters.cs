using System;
using org.bouncycastle.crypto;
using org.bouncycastle.math;

namespace org.bouncycastle.crypto.parameters
{
	public class ElGamalParameters : CipherParameters
	{
		private BigInteger              g;
		private BigInteger              p;

		public ElGamalParameters(
			BigInteger  p,
			BigInteger  g)
		{
			this.g = g;
			this.p = p;
		}

		public BigInteger getP()
		{
			return p;
		}

		/**
			* return the generator - g
			*/
		public BigInteger getG()
		{
			return g;
		}

		public override bool Equals(
			Object  obj)
		{
			if (!(typeof(ElGamalParameters).IsInstanceOfType(obj))) return false;
			ElGamalParameters    pm = (ElGamalParameters)obj;
			return pm.getP().Equals(p) && pm.getG().Equals(g);
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

	}

}