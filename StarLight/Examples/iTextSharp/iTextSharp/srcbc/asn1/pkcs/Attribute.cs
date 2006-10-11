using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    public class Attribute
        : ASN1Encodable
    {
        private DERObjectIdentifier attrType;
        private ASN1Set             attrValues;
    
        /**
         * return an Attribute object from the given object.
         *
         * @param o the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static Attribute getInstance(
            object o)
        {
            if (o == null || o is Attribute)
            {
                return (Attribute)o;
            }
            
            if (o is ASN1Sequence)
            {
                return new Attribute((ASN1Sequence)o);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
        
        public Attribute(
            ASN1Sequence seq)
        {
            attrType = (DERObjectIdentifier)seq.getObjectAt(0);
            attrValues = (ASN1Set)seq.getObjectAt(1);
        }
    
        public Attribute(
            DERObjectIdentifier attrType,
            ASN1Set             attrValues)
        {
            this.attrType = attrType;
            this.attrValues = attrValues;
        }
    
        public DERObjectIdentifier getAttrType()
        {
            return attrType;
        }
        
        public ASN1Set getAttrValues()
        {
            return attrValues;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * Attribute ::= SEQUENCE {
         *     attrType OBJECT IDENTIFIER,
         *     attrValues SET OF AttributeValue
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(attrType);
            v.add(attrValues);
    
            return new DERSequence(v);
        }
    }
}
