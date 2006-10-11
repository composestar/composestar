using org.bouncycastle.asn1;
using org.bouncycastle.math;
using org.bouncycastle.math.ec;

using System;

namespace org.bouncycastle.asn1.x9
{
    /**
     * ASN.1 def for Elliptic-Curve ECParameters structure. See
     * X9.62, for further details.
     */
    public class X9ECParameters
        : ASN1Encodable//, X9ObjectIdentifiers
    {
        private static BigInteger   ONE = BigInteger.valueOf(1);
    
        private X9FieldID           fieldID;
        private ECCurve             curve;
        private ECPoint             g;
        private BigInteger          n;
        private BigInteger          h;
        private byte[]              seed;
    
        public X9ECParameters(
            ASN1Sequence  seq)
        {
            if (!(seq.getObjectAt(0) is DERInteger)
               || !((DERInteger)seq.getObjectAt(0)).getValue().Equals(ONE))
            {
                throw new ArgumentException("bad version in X9ECParameters");
            }


          
           

            X9Curve x9c = null;

            if (seq.getObjectAt(2) is X9Curve)
            {
                x9c = (X9Curve)seq.getObjectAt(2);
            }
            else
            {
                x9c = new X9Curve(new X9FieldID((ASN1Sequence)seq.getObjectAt(1)), (ASN1Sequence)seq.getObjectAt(2));
            }
            
            this.curve = x9c.getCurve();

            if (seq.getObjectAt(3) is X9ECPoint)
            {
                this.g = ((X9ECPoint)seq.getObjectAt(3)).getPoint();
            }
            else
            {
                this.g = new X9ECPoint(curve, (ASN1OctetString)seq.getObjectAt(3)).getPoint();
            }


            this.n = ((DERInteger)seq.getObjectAt(4)).getValue();
            this.seed = x9c.getSeed();
    
            if (seq.size() == 6)
            {
                this.h = ((DERInteger)seq.getObjectAt(5)).getValue();
            }
            else
            {
                this.h = ONE;
            }
        }
    
        public X9ECParameters(
            ECCurve     curve,
            ECPoint     g,
            BigInteger  n)
            : this(curve, g, n, ONE, null)
        {
        }
    
        public X9ECParameters(
            ECCurve     curve,
            ECPoint     g,
            BigInteger  n,
            BigInteger  h)
            : this(curve, g, n, h, null)
        {
        }
    
        public X9ECParameters(
            ECCurve     curve,
            ECPoint     g,
            BigInteger  n,
            BigInteger  h,
            byte[]      seed)
        {
            this.curve = curve;
            this.g = g;
            this.n = n;
            this.h = h;
            this.seed = seed;
    
            if (curve is ECCurve.Fp)
            {
                this.fieldID = new X9FieldID(X9ObjectIdentifiers.prime_field, ((ECCurve.Fp)curve).getQ());
            }
            else
            {
                this.fieldID = new X9FieldID(X9ObjectIdentifiers.characteristic_two_field, null);
            }
        }
    
        public ECCurve getCurve()
        {
            return curve;
        }
    
        public ECPoint getG()
        {
            return g;
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
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  ECParameters ::= SEQUENCE {
         *      version         INTEGER { ecpVer1(1) } (ecpVer1),
         *      fieldID         FieldID {{FieldTypes}},
         *      curve           X9Curve,
         *      base            X9ECPoint,
         *      order           INTEGER,
         *      cofactor        INTEGER OPTIONAL
         *  }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(new DERInteger(1));
            v.add(fieldID);
            v.add(new X9Curve(curve, seed));
            v.add(new X9ECPoint(g));
            v.add(new DERInteger(n));
    
            if (!h.Equals(BigInteger.valueOf(1)))
            {
                v.add(new DERInteger(h));
            }
    
            return new DERSequence(v);
        }
    }
}
