using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class EncryptedContentInfo
        : ASN1Encodable
    {
        private DERObjectIdentifier contentType;
        private AlgorithmIdentifier contentEncryptionAlgorithm;
        private ASN1OctetString     encryptedContent;
        
        public EncryptedContentInfo(
            DERObjectIdentifier contentType, 
            AlgorithmIdentifier contentEncryptionAlgorithm,
            ASN1OctetString     encryptedContent)
        {
            this.contentType = contentType;
            this.contentEncryptionAlgorithm = contentEncryptionAlgorithm;
            this.encryptedContent = encryptedContent;
        }
        
        public EncryptedContentInfo(
            ASN1Sequence seq)
        {
            contentType = (DERObjectIdentifier)seq.getObjectAt(0);
            contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance(
                                                            seq.getObjectAt(1));
            if (seq.size() > 2)
            {
                encryptedContent = ASN1OctetString.getInstance(
                                    (ASN1TaggedObject)seq.getObjectAt(2), false);
            }
        }
    
        /**
         * return an EncryptedContentInfo object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static EncryptedContentInfo getInstance(
            object obj)
        {
            if (obj == null || obj is EncryptedContentInfo)
            {
                return (EncryptedContentInfo)obj;
            }
            
            if (obj is ASN1Sequence)
            {
                return new EncryptedContentInfo((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid EncryptedContentInfo: "
                                                    + obj.GetType().Name);
        }
    
        public DERObjectIdentifier getContentType()
        {
            return contentType;
        }
    
        public AlgorithmIdentifier getContentEncryptionAlgorithm()
        {
            return contentEncryptionAlgorithm;
        }
    
        public ASN1OctetString getEncryptedContent()
        {
            return encryptedContent;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * EncryptedContentInfo ::= SEQUENCE {
         *     contentType ContentType,
         *     contentEncryptionAlgorithm ContentEncryptionAlgorithmIdentifier,
         *     encryptedContent [0] IMPLICIT EncryptedContent OPTIONAL 
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
            
            v.add(contentType);
            v.add(contentEncryptionAlgorithm);
    
            if (encryptedContent != null)
            {
                v.add(new BERTaggedObject(false, 0, encryptedContent));
            }
            
            return new BERSequence(v);
        }
    }
}
