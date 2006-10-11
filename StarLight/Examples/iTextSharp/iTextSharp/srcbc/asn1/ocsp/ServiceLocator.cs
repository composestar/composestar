using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.ocsp
{
    public class ServiceLocator
        : ASN1Encodable
    {
        internal X509Name    issuer = null;
        internal ASN1Object    locator = null;
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * ServiceLocator ::= SEQUENCE {
         *     issuer    Name,
         *     locator   AuthorityInfoAccessSyntax OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector    v = new ASN1EncodableVector();
    
            v.add(issuer);
    
            if (locator != null)
            {
                v.add(locator);
            }
    
            return new DERSequence(v);
        }
    }
}
