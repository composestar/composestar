using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class OtherRecipientInfo
        : ASN1Encodable
    {
        private DERObjectIdentifier    oriType;
        private ASN1Encodable           oriValue;
    
        public OtherRecipientInfo(
            DERObjectIdentifier     oriType,
            ASN1Encodable            oriValue)
        {
            this.oriType = oriType;
            this.oriValue = oriValue;
        }
        
        public OtherRecipientInfo(
            ASN1Sequence seq)
        {
            oriType = DERObjectIdentifier.getInstance(seq.getObjectAt(1));
            oriValue = seq.getObjectAt(2);
        }
    
        /**
         * return a OtherRecipientInfo object from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static OtherRecipientInfo getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        /**
         * return a OtherRecipientInfo object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static OtherRecipientInfo getInstance(
            object obj)
        {
            if (obj == null || obj is OtherRecipientInfo)
            {
                return (OtherRecipientInfo)obj;
            }
            
            if (obj is ASN1Sequence)
            {
                return new OtherRecipientInfo((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid OtherRecipientInfo: " + obj.GetType().Name);
        }
    
        public DERObjectIdentifier getType()
        {
            return oriType;
        }
    
        public ASN1Encodable getValue()
        {
            return oriValue;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * OtherRecipientInfo ::= SEQUENCE {
         *    oriType OBJECT IDENTIFIER,
         *    oriValue ANY DEFINED BY oriType }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(oriType);
            v.add(oriValue);
    
            return new DERSequence(v);
        }
    }
}
