using System;
using org.bouncycastle.math;

namespace org.bouncycastle.math.ec
{
	public abstract class ECFieldElement
	{
		BigInteger x;
		BigInteger p;

		protected ECFieldElement(BigInteger q, BigInteger x)
		{
			if (x.compareTo(q) >= 0)
			{
				throw new ArgumentException("x value too large in field element");
			}

			this.x = x;
			this.p = q; // curve.getQ();
		}

		public BigInteger toBigInteger()
		{
			return x;
		}

		public override bool Equals(Object other)
		{
			if ( other == this ) return true;

			if (!(typeof(ECFieldElement).IsInstanceOfType(other))) return false;
			ECFieldElement o = (ECFieldElement)other;
			return p.Equals(o.p) && x.Equals(o.x);
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}

		public abstract String         getFieldName();
		public abstract ECFieldElement add(ECFieldElement b);
		public abstract ECFieldElement subtract(ECFieldElement b);
		public abstract ECFieldElement multiply(ECFieldElement b);
		public abstract ECFieldElement divide(ECFieldElement b);
		public abstract ECFieldElement negate();
		public abstract ECFieldElement square();
		public abstract ECFieldElement invert();
		public abstract ECFieldElement sqrt();

		public class Fp : ECFieldElement
		{
			/**
			* return the field name for this field.
			*
			* @return the string "Fp".
			*/
			public override String getFieldName()
			{
				return "Fp";
			}

			public Fp(BigInteger q, BigInteger x) : base(q, x) 	{}

			public override ECFieldElement add(ECFieldElement b)
			{
				return new Fp(p, x.add(b.x).mod(p));
			}

			public override ECFieldElement subtract(ECFieldElement b)
			{
				return new Fp(p, x.subtract(b.x).mod(p));
			}

			public override ECFieldElement multiply(ECFieldElement b)
			{
				return new Fp(p, x.multiply(b.x).mod(p));
			}

			public override ECFieldElement divide(ECFieldElement b)
			{
				return new Fp(p, x.multiply(b.x.modInverse(p)).mod(p));
			}

			public override ECFieldElement negate()
			{
				return new Fp(p, x.negate().mod(p));
			}

			public override ECFieldElement square()
			{
				return new Fp(p, x.multiply(x).mod(p));
			}

			public override ECFieldElement invert()
			{
				return new Fp(p, x.modInverse(p));
			}

			// D.1.4 91
			/**
			 * return a sqrt root - the routine verifies that the calculation
			 * returns the right value - if none exists it returns null.
			 */
			public override ECFieldElement sqrt()
			{
				// p mod 4 == 3
				if ( p.testBit(1) )
				{
					// z = g^(u+1) + p, p = 4u + 3
					ECFieldElement z = new Fp(p, x.modPow(p.shiftRight(2).add(ECConstants.ONE), p));

					return z.square().Equals(this) ? z : null;
				}

				throw new Exception("not done yet");
			}
		}
	}

}