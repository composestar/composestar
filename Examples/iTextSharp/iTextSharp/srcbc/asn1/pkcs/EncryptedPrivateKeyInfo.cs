using org.bouncycastle.asn1.x509;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    public class EncryptedPrivateKeyInfo
        : ASN1Encodable//, PKCSObjectIdentifiers
    {
        private AlgorithmIdentifier algId;
        private ASN1OctetString     data;
    
        public EncryptedPrivateKeyInfo(
            ASN1Sequence  seq)
        {
            IEnumerator e = seq.getObjects();
            e.MoveNext();
            algId = new AlgorithmIdentifier((ASN1Sequence)e.Current);
            e.MoveNext();
            data = (ASN1OctetString)e.Current;
        }
    
        public EncryptedPrivateKeyInfo(
            AlgorithmIdentifier algId,
            byte[]              encoding)
        {
            this.algId = algId;
            this.data = new DEROctetString(encoding);
        }
    
        public static EncryptedPrivateKeyInfo getInstance(
             object  obj)
        {
             if (obj is EncryptedPrivateKeyInfo)
             {
                 return (EncryptedPrivateKeyInfo)obj;
             }
             else if (obj is ASN1Sequence)
             {
                 return new EncryptedPrivateKeyInfo((ASN1Sequence)obj);
             }
                                                                                
             throw new ArgumentException("unknown object in factory");
        }

        public AlgorithmIdentifier getEncryptionAlgorithm()
        {
            return algId;
        }
    
        public byte[] getEncryptedData()
        {
            return data.getOctets();
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * EncryptedPrivateKeyInfo ::= SEQUENCE {
         *      encryptionAlgorithm AlgorithmIdentifier {{KeyEncryptionAlgorithms}},
         *      encryptedData EncryptedData
         * }
         *
         * EncryptedData ::= OCTET STRING
         *
         * KeyEncryptionAlgorithms ALGORITHM-IDENTIFIER ::= {
         *          ... -- For local profiles
         * }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(algId);
            v.add(data);
    
            return new DERSequence(v);
        }
    }
}
