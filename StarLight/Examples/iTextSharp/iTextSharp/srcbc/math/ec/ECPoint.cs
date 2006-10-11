using System;
using org.bouncycastle.math;

namespace org.bouncycastle.math.ec
{
	/**
	 * base class for points on elliptic curves.
	 */
	public abstract class ECPoint
	{
		ECCurve        curve;
		ECFieldElement x;
		ECFieldElement y;

		protected ECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y)
		{
			this.curve = curve;
			this.x = x;
			this.y = y;
		}
		
		public ECFieldElement getX()
		{
			return x;
		}

		public ECFieldElement getY()
		{
			return y;
		}

		public override bool Equals(
			Object  other)
		{
			if ( other == this )
				return true;
			if (!(typeof(ECPoint).IsInstanceOfType(other))) return false;

			ECPoint o = (ECPoint)other;

			return x.Equals(o.x) && y.Equals(o.y);
		}
		public override int GetHashCode()
		{
			return base.GetHashCode();
		}


		public abstract byte[] getEncoded();

		public abstract ECPoint add(ECPoint b);
		public abstract ECPoint subtract(ECPoint b);
		public abstract ECPoint twice();
		public abstract ECPoint multiply(BigInteger b);

		/**
		 * Elliptic curve points over Fp
		 */
		public class Fp :ECPoint
		{
			public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y)
				: base(curve, x, y) { }

			/**
			* return the field element encoded with point compression. (S 4.3.6)
			*/
			public override byte[] getEncoded()
			{
				byte    PC;

				if (this.getY().toBigInteger().testBit(0))
				{
					PC = 0x02;
				}
				else
				{
					PC = 0x03;
				}

				byte[]  X = this.getX().toBigInteger().toByteArray();
				byte[]  PO = new byte[X.Length + 1];

				PO[0] = PC;
				Array.Copy(X, 0, PO, 1, X.Length);

				return PO;
			}

			// B.3 pg 62
			public override ECPoint add(ECPoint b)
			{
				ECFieldElement gamma = b.y.subtract(y).divide(b.x.subtract(x));

				ECFieldElement x3 = gamma.multiply(gamma).subtract(x).subtract(b.x);
				ECFieldElement y3 = gamma.multiply(x.subtract(x3)).subtract(y);

				return new ECPoint.Fp(curve, x3, y3);
			}

			// B.3 pg 62
			public override ECPoint twice()
			{
				ECFieldElement TWO = curve.fromBigInteger(BigInteger.valueOf(2));
				ECFieldElement THREE = curve.fromBigInteger(BigInteger.valueOf(3));
				ECFieldElement gamma = x.multiply(x).multiply(THREE).add(curve.getA()).divide(y.multiply(TWO));

				ECFieldElement x3 = gamma.multiply(gamma).subtract(x.multiply(TWO));
				ECFieldElement y3 = gamma.multiply(x.subtract(x3)).subtract(y);
	                
				return new ECPoint.Fp(curve, x3, y3);
			}

			// D.3.2 pg 102 (see Note:)
			public override ECPoint subtract(ECPoint p2)
			{
				return add(new ECPoint.Fp(curve, p2.x, p2.y.negate()));
			}

			// D.3.2 pg 101
			public override ECPoint multiply(BigInteger k)
			{
				// BigInteger e = k.mod(n); // n == order this
				BigInteger e = k;

				BigInteger h = e.multiply(BigInteger.valueOf(3));

				ECPoint R = this;

				for (int i = h.bitLength() - 2; i > 0; i--)
				{             
					R = R.twice();       

					if ( h.testBit(i) && !e.testBit(i) )
					{                    
						//System.out.print("+");
						R = R.add(this);
					}
					else if ( !h.testBit(i) && e.testBit(i) )
					{
						//System.out.print("-");
						R = R.subtract(this);
					}
					// else
					// System.out.print(".");
				}
				// System.out.println();

				return R;
			}
		}
	}

}