using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

namespace org.bouncycastle.asn1.ocsp
{
    public class ResponderID
        : ASN1Encodable
    {
        private ASN1Encodable    value;
    
        public ResponderID(
            ASN1OctetString    value)
        {
            this.value = value;
        }
    
        public ResponderID(
            X509Name    value)
        {
            this.value = value;
        }
    
        public static ResponderID getInstance(
            object  obj)
        {
            if (obj == null || obj is ResponderID)
            {
                return (ResponderID)obj;
            }
            else if (obj is DEROctetString)
            {
                return new ResponderID((DEROctetString)obj);
            }
            else if (obj is ASN1TaggedObject)
            {
                ASN1TaggedObject    o = (ASN1TaggedObject)obj;
    
                if ((int)o.getTagNo() == 1)
                {
                    return new ResponderID(X509Name.getInstance(o, true));
                }
                else
                {
                    return new ResponderID(ASN1OctetString.getInstance(o, true));
                }
            }
    
            return new ResponderID(X509Name.getInstance(obj));
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * ResponderID ::= CHOICE {
         *      byName          [1] Name,
         *      byKey           [2] KeyHash }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            if (value is ASN1OctetString)
            {
                return new DERTaggedObject(true, 2, value);
            }
    
            return new DERTaggedObject(true, 1, value);
        }
    }
}
