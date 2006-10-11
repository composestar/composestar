using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class RecipientIdentifier
        : ASN1Encodable
    {
        private ASN1Encodable id;
        
        public RecipientIdentifier(
            IssuerAndSerialNumber id)
        {
            this.id = id;
        }
        
        public RecipientIdentifier(
            ASN1OctetString id)
        {
            this.id = new DERTaggedObject(false, 0, id);
        }
        
        public RecipientIdentifier(
            ASN1Object id)
        {
            this.id = id;
        }
        
        /**
         * return a RecipientIdentifier object from the given object.
         *
         * @param o the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static RecipientIdentifier getInstance(
            object o)
        {
            if (o == null || o is RecipientIdentifier)
            {
                return (RecipientIdentifier)o;
            }
            
            if (o is IssuerAndSerialNumber)
            {
                return new RecipientIdentifier((IssuerAndSerialNumber)o);
            }
            
            if (o is ASN1OctetString)
            {
                return new RecipientIdentifier((ASN1OctetString)o);
            }
            
            if (o is ASN1Object)
            {
                return new RecipientIdentifier((ASN1Object)o);
            }
            
            throw new ArgumentException(
              "Illegal object in RecipientIdentifier: " + o.GetType().Name);
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
    
            return IssuerAndSerialNumber.getInstance(id);
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * RecipientIdentifier ::= CHOICE {
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
