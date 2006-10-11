using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class KeyTransRecipientInfo
        : ASN1Encodable
    {
        private DERInteger          version;
        private RecipientIdentifier rid;
        private AlgorithmIdentifier keyEncryptionAlgorithm;
        private ASN1OctetString     encryptedKey;
    
        public KeyTransRecipientInfo(
            RecipientIdentifier rid,
            AlgorithmIdentifier keyEncryptionAlgorithm,
            ASN1OctetString     encryptedKey)
        {
            if (rid.toASN1Object() is ASN1TaggedObject)
            {
                this.version = new DERInteger(2);
            }
            else
            {
                this.version = new DERInteger(0);
            }
    
            this.rid = rid;
            this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
            this.encryptedKey = encryptedKey;
        }
        
        public KeyTransRecipientInfo(
            ASN1Sequence seq)
        {
            this.version = (DERInteger)seq.getObjectAt(0);
            this.rid = RecipientIdentifier.getInstance(seq.getObjectAt(1));
            this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(2));
            this.encryptedKey = (ASN1OctetString)seq.getObjectAt(3);
        }
    
        /**
         * return a KeyTransRecipientInfo object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static KeyTransRecipientInfo getInstance(
            object obj)
        {
            if (obj == null || obj is KeyTransRecipientInfo)
            {
                return (KeyTransRecipientInfo)obj;
            }
            
            if(obj is ASN1Sequence)
            {
                return new KeyTransRecipientInfo((ASN1Sequence)obj);
            }
            
            throw new ArgumentException(
            "Illegal object in KeyTransRecipientInfo: " + obj.GetType().Name);
        } 
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public RecipientIdentifier getRecipientIdentifier()
        {
            return rid;
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
         * KeyTransRecipientInfo ::= SEQUENCE {
         *     version CMSVersion,  -- always set to 0 or 2
         *     rid RecipientIdentifier,
         *     keyEncryptionAlgorithm KeyEncryptionAlgorithmIdentifier,
         *     encryptedKey EncryptedKey 
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(rid);
            v.add(keyEncryptionAlgorithm);
            v.add(encryptedKey);
    
            return new DERSequence(v);
        }
    }
}
