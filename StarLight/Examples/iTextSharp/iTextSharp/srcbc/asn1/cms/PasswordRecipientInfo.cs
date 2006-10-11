using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.cms
{
    public class PasswordRecipientInfo
        : ASN1Encodable
    {
        private DERInteger          version;
        private AlgorithmIdentifier keyDerivationAlgorithm;
        private AlgorithmIdentifier keyEncryptionAlgorithm;
        private ASN1OctetString     encryptedKey;
    
        public PasswordRecipientInfo(
            AlgorithmIdentifier     keyEncryptionAlgorithm,
            ASN1OctetString         encryptedKey)
        {
            this.version = new DERInteger(0);
            this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
            this.encryptedKey = encryptedKey;
        }
        
        public PasswordRecipientInfo(
            ASN1Sequence seq)
        {
            version = (DERInteger)seq.getObjectAt(0);
            if (seq.getObjectAt(1) is ASN1TaggedObject)
            {
                keyDerivationAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)seq.getObjectAt(1), false);
                keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(2));
                encryptedKey = (ASN1OctetString)seq.getObjectAt(3);
            }
            else
            {
                keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
                encryptedKey = (ASN1OctetString)seq.getObjectAt(2);
            }
        }
    
        /**
         * return a PasswordRecipientInfo object from a tagged object.
         *
         * @param obj the tagged object holding the object we want.
         * @param explicit true if the object is meant to be explicitly
         *              tagged false otherwise.
         * @exception IllegalArgumentException if the object held by the
         *          tagged object cannot be converted.
         */
        public static PasswordRecipientInfo getInstance(
            ASN1TaggedObject    obj,
            bool             explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
        
        /**
         * return a PasswordRecipientInfo object from the given object.
         *
         * @param obj the object we want converted.
         * @exception IllegalArgumentException if the object cannot be converted.
         */
        public static PasswordRecipientInfo getInstance(
            object obj)
        {
            if (obj == null || obj is PasswordRecipientInfo)
            {
                return (PasswordRecipientInfo)obj;
            }
            
            if(obj is ASN1Sequence)
            {
                return new PasswordRecipientInfo((ASN1Sequence)obj);
            }
            
            throw new ArgumentException("Invalid PasswordRecipientInfo: " + obj.GetType().Name);
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public AlgorithmIdentifier getKeyDerivationAlgorithm()
        {
            return keyDerivationAlgorithm;
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
         * PasswordRecipientInfo ::= SEQUENCE {
         *   version CMSVersion,   -- Always set to 0
         *   keyDerivationAlgorithm [0] KeyDerivationAlgorithmIdentifier
         *                             OPTIONAL,
         *  keyEncryptionAlgorithm KeyEncryptionAlgorithmIdentifier,
         *  encryptedKey EncryptedKey }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            
            if (keyDerivationAlgorithm != null)
            {
                v.add(new DERTaggedObject(false, 0, keyDerivationAlgorithm));
            }
            v.add(keyEncryptionAlgorithm);
            v.add(encryptedKey);
    
            return new DERSequence(v);
        }
    }
}
