using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class KEKRecipientInfo
        : ASN1Encodable
    {
        private DERInteger          version;
        private KEKIdentifier       kekid;
        private AlgorithmIdentifier keyEncryptionAlgorithm;
        private ASN1OctetString     encryptedKey;
    
        public KEKRecipientInfo(
            KEKIdentifier       kekid,
            AlgorithmIdentifier keyEncryptionAlgorithm,
            ASN1OctetString     encryptedKey)
        {
            this.version = new DERInteger(4);
            this.kekid = kekid;
            this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
            this.encryptedKey = encryptedKey;
        }
        
        public KEKRecipientInfo(
            ASN1Sequence seq)
        {
            version = (DERInteger)seq.getObjectAt(0);
            kekid = KEKIdentifier.getInstance(seq.getObjectAt(1));
            keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(2));
            encryptedKey = (ASN1OctetString)seq.getObjectAt(3);
        }
    
        /**
         * return a KEKRecipientInfo object from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static KEKRecipientInfo getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        /**
         * return a KEKRecipientInfo object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static KEKRecipientInfo getInstance(
            object obj)
        {
            if (obj == null || obj is KEKRecipientInfo)
            {
                return (KEKRecipientInfo)obj;
            }
            
            if(obj is ASN1Sequence)
            {
                return new KEKRecipientInfo((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid KEKRecipientInfo: " + obj.GetType().Name);
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
        
        public KEKIdentifier getKekid()
        {
            return kekid;
        }
    
        public AlgorithmIdentifier getKeyEncryptionAlgorithm()
        {
            return keyEncryptionAlgorithm;
        }
    
        public ASN1OctetString getEncryptedKey()
        {
            return encryptedKey;
        }
    
        /** 
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * KEKRecipientInfo ::= SEQUENCE {
         *     version CMSVersion,  -- always set to 4
         *     kekid KEKIdentifier,
         *     keyEncryptionAlgorithm KeyEncryptionAlgorithmIdentifier,
         *     encryptedKey EncryptedKey 
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(kekid);
            v.add(keyEncryptionAlgorithm);
            v.add(encryptedKey);
    
            return new DERSequence(v);
        }
    }
}
