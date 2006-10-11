using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class OriginatorIdentifierOrKey
        : ASN1Encodable
    {
        private ASN1Encodable id;
        
        public OriginatorIdentifierOrKey(
            IssuerAndSerialNumber id)
        {
            this.id = id;
        }
        
        public OriginatorIdentifierOrKey(
            ASN1OctetString id)
        {
            this.id = new DERTaggedObject(false, 0, id);
        }
        
        public OriginatorIdentifierOrKey(
            OriginatorPublicKey id)
        {
            this.id = new DERTaggedObject(false, 1, id);
        }
        
        public OriginatorIdentifierOrKey(
            ASN1Object id)
        {
            this.id = id;
        }
        
        /**
         * return an OriginatorIdentifierOrKey object from a tagged object.
         *
         * @param o the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static OriginatorIdentifierOrKey getInstance(
            ASN1TaggedObject    o,
            bool             explicitly)
        {
            if (!explicitly)
            {
                throw new ArgumentException(
                        "Can't implicitly tag OriginatorIdentifierOrKey");
            }
    
            return getInstance(o.getObject());
        }
        
        /**
         * return an OriginatorIdentifierOrKey object from the given object.
         *
         * @param o the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static OriginatorIdentifierOrKey getInstance(
            object o)
        {
            if (o == null || o is OriginatorIdentifierOrKey)
            {
                return (OriginatorIdentifierOrKey)o;
            }
            
            if (o is ASN1Object)
            {
                return new OriginatorIdentifierOrKey((ASN1Object)o);
            }
            
            throw new ArgumentException("Invalid OriginatorIdentifierOrKey: " + o.GetType().Name);
        } 
    
        public ASN1Encodable getId()
        {
            return id;
        }
        
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * OriginatorIdentifierOrKey ::= CHOICE {
         *     issuerAndSerialNumber IssuerAndSerialNumber,
         *     subjectKeyIdentifier [0] SubjectKeyIdentifier,
         *     originatorKey [1] OriginatorPublicKey 
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
