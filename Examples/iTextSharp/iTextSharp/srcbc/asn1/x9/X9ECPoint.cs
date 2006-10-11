using org.bouncycastle.asn1;
using org.bouncycastle.math.ec;

namespace org.bouncycastle.asn1.x9
{
    /**
     * class for describing an ECPoint as a DER object.
     */
    public class X9ECPoint
        : ASN1Encodable
    {
        ECPoint p;
    
        public X9ECPoint(
            ECPoint p)
        {
            this.p = p;
        }
    
        public X9ECPoint(
            ECCurve          c,
            ASN1OctetString  s)
        {
            this.p = c.decodePoint(s.getOctets());
        }
    
        public ECPoint getPoint()
        {
            return p;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  ECPoint ::= OCTET STRING
         * </pre>
         * <p>
         * Octet string produced using ECPoint.getEncoded().
         */
        public override ASN1Object toASN1Object()
        {
            return new DEROctetString(p.getEncoded());
        }
    }
}
