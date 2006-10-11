using System;
using org.bouncycastle.math;

namespace org.bouncycastle.math.ec
{
    /**
     * base class for an elliptic curve
     */

    public abstract class ECCurve
    {
        BigInteger q;
        ECFieldElement a, b;

        public ECCurve(BigInteger q, BigInteger a, BigInteger b)
        {
            this.q = q;
            this.a = fromBigInteger(a);
            this.b = fromBigInteger(b);
        }

        public abstract ECFieldElement fromBigInteger(BigInteger x);

        public abstract ECPoint decodePoint(byte[] encoded);

        public abstract BigInteger getQ();

        public ECFieldElement getA()
        {
            return a;
        }

        public ECFieldElement getB()
        {
            return b;
        }

        public override bool Equals(object obj)
        {
            if (obj is ECCurve)
            {
                return (q.Equals(((ECCurve)obj).getQ()) && a.Equals(((ECCurve)obj).getA()) && b.Equals(((ECCurve)obj).getB()));
            }
            return base.Equals(obj);
        }

        public override int GetHashCode()
        {
            return q.GetHashCode() ^ a.GetHashCode() ^ b.GetHashCode();
        }

        /**
         * Elliptic curve over Fp
         */
        public class Fp : ECCurve
        {
            public Fp(BigInteger q, BigInteger a, BigInteger b) 
                : base(q, a, b)    {}

            public override BigInteger getQ()
            {
                return q;
            }

            public override ECFieldElement fromBigInteger(BigInteger x)
            {
                return new ECFieldElement.Fp(this.q, x);
            }

            /**
            * decode a point on this curve which has been encoded using
            * point compression (X9.62 s 4.2.1 pg 17) returning the point.
            */
            public override ECPoint decodePoint(byte[] encoded)
            {
                ECPoint p = null;

                switch (encoded[0])
                {
                        // compressed
                    case 0x02:
                    case 0x03:
                        int ytilde = encoded[0] & 1;
                        byte[]  i = new byte[encoded.Length - 1];

                        Array.Copy(encoded, 1, i, 0, i.Length);

                        ECFieldElement x = new ECFieldElement.Fp(this.q, new BigInteger(1, i));
                        ECFieldElement alpha = x.multiply(x.square()).add(x.multiply(a).add(b));
                        ECFieldElement beta = alpha.sqrt();

                        //
                        // if we can't find a sqrt we haven't got a point on the
                        // curve - run!
                        //
                        if (beta == null)
                        {
                            throw new Exception("Invalid point compression");
                        }

                        int bit0 = (beta.toBigInteger().testBit(0) ? 0 : 1);

                        if ( bit0 == ytilde )
                        {
                            p = new ECPoint.Fp(this, x, beta);
                        }
                        else
                        {
                            p = new ECPoint.Fp(this, x,
                                new ECFieldElement.Fp(this.q, q.subtract(beta.toBigInteger())));
                        }
                        break;
                    case 0x04:
                        byte[]  xEnc = new byte[(encoded.Length - 1) / 2];
                        byte[]  yEnc = new byte[(encoded.Length - 1) / 2];

                        Array.Copy(encoded, 1, xEnc, 0, xEnc.Length);
                        Array.Copy(encoded, xEnc.Length + 1, yEnc, 0, yEnc.Length);

                        p = new ECPoint.Fp(this,
                            new ECFieldElement.Fp(this.q, new BigInteger(1, xEnc)),
                            new ECFieldElement.Fp(this.q, new BigInteger(1, yEnc)));
                        break;
                    default:
                        throw new Exception("Invalid point encoding 0x" + encoded[0].ToString("x"));
                }

                return p;
            }

            public override bool Equals(object obj)
            {
                return base.Equals(obj);
            }

            public override int GetHashCode()
            {
                return q.GetHashCode() ^ a.GetHashCode() ^ b.GetHashCode();
            }
        }
    }
}
