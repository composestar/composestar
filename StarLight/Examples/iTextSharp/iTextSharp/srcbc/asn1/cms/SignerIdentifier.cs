using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class SignerIdentifier
        : ASN1Encodable
    {
        private ASN1Encodable id;
        
        public SignerIdentifier(
            IssuerAndSerialNumber id)
        {
            this.id = id;
        }
        
        public SignerIdentifier(
            ASN1OctetString id)
        {
            this.id = new DERTaggedObject(false, 0, id);
        }
        
        public SignerIdentifier(
            ASN1Object id)
        {
            this.id = id;
        }
        
        /**
         * return a SignerIdentifier object from the given object.
         *
         * @param o the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static SignerIdentifier getInstance(
            object o)
        {
            if (o == null || o is SignerIdentifier)
            {
                return (SignerIdentifier)o;
            }
            
            if (o is IssuerAndSerialNumber)
            {
                return new SignerIdentifier((IssuerAndSerialNumber)o);
            }
            
            if (o is ASN1OctetString)
            {
                return new SignerIdentifier((ASN1OctetString)o);
            }
            
            if (o is ASN1Object)
            {
                return new SignerIdentifier((ASN1Object)o);
            }
            
            throw new ArgumentException(
                 "Illegal object in SignerIdentifier: " + o.GetType().Name);
        } 
    
        public bool isTagged()
        {
            return (id is ASN1TaggedObject);
        }
    
        public ASN1Encodable getId()
        {
            if (id is ASN1TaggedObject)
            {
                return ASN1OctetString.getInstance((ASN1TaggedObject)id, false);
            }
    
            return id;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * SignerIdentifier ::= CHOICE {
         *     issuerAndSerialNumber IssuerAndSerialNumber,
         *     subjectKeyIdentifier [0] SubjectKeyIdentifier 
         * }
         *
         * SubjectKeyIdentifier ::= OCTET STRING
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            return id.toASN1Object();
        }
    }
}
