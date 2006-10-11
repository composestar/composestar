using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class OtherKeyAttribute
        : ASN1Encodable
    {
        private DERObjectIdentifier keyAttrId;
        private ASN1Encodable        keyAttr;
    
        /**
         * return an OtherKeyAttribute object from the given object.
         *
         * @param o the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static OtherKeyAttribute getInstance(
            object o)
        {
            if (o == null || o is OtherKeyAttribute)
            {
                return (OtherKeyAttribute)o;
            }
            
            if (o is ASN1Sequence)
            {
                return new OtherKeyAttribute((ASN1Sequence)o);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
        
        public OtherKeyAttribute(
            ASN1Sequence seq)
        {
            keyAttrId = (DERObjectIdentifier)seq.getObjectAt(0);
            keyAttr = seq.getObjectAt(1);
        }
    
        public OtherKeyAttribute(
            DERObjectIdentifier keyAttrId,
            ASN1Encodable        keyAttr)
        {
            this.keyAttrId = keyAttrId;
            this.keyAttr = keyAttr;
        }
    
        public DERObjectIdentifier getKeyAttrId()
        {
            return keyAttrId;
        }
        
        public ASN1Encodable getKeyAttr()
        {
            return keyAttr;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * OtherKeyAttribute ::= SEQUENCE {
         *     keyAttrId OBJECT IDENTIFIER,
         *     keyAttr ANY DEFINED BY keyAttrId OPTIONAL
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(keyAttrId);
            v.add(keyAttr);
    
            return new DERSequence(v);
        }
    }
}
