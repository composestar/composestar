using org.bouncycastle.asn1;
using org.bouncycastle.math;
using org.bouncycastle.math.ec;

using System;

namespace org.bouncycastle.asn1.x9
{
    /**
     * class for processing an FieldElement as a DER object.
     */
    public class X9FieldElement
        : ASN1Encodable
    {
        private ECFieldElement  f;
    
        public X9FieldElement(
            ECFieldElement  f)
        {
            this.f = f;
        }
    
        public X9FieldElement(
            bool          fP,
            BigInteger       q,
            ASN1OctetString  s)
        {
            if (fP)
            {
                this.f = new ECFieldElement.Fp(q, new BigInteger(1, s.getOctets()));
            }
            else
            {
                throw new Exception("not implemented");
            }
        }
    
        public ECFieldElement getValue()
        {
            return f;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         *  FieldElement ::= OCTET STRING
         * </pre>
         * <p>
         * <ol>
         * <li> if <i>q</i> is an odd prime then the field element is
         * processed as an Integer and converted to an octet string
         * according to x 9.62 4.3.1.</li>
         * <li> if <i>q</i> is 2<sup>m</sup> then the bit string
         * contained in the field element is converted into an octet
         * string with the same ordering padded at the front if necessary.
         * </li>
         * </ol>
         */
        public override ASN1Object toASN1Object()
        {
            return new DEROctetString(f.toBigInteger().toByteArray());
        }
    }
}
