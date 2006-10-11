using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.math;

using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1.pkcs
{
    public class PrivateKeyInfo
        : ASN1Encodable//, PKCSObjectIdentifiers
    {
        private ASN1Object               privKey;
        private AlgorithmIdentifier     algId;

        public static PrivateKeyInfo getInstance(
            object obj)
        {
            if (obj is PrivateKeyInfo || obj == null)
            {
                return (PrivateKeyInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new PrivateKeyInfo((ASN1Sequence)obj);
            }

            throw new ArgumentException("unknown object in factory");
        }
        
        public PrivateKeyInfo(
            AlgorithmIdentifier algId,
            ASN1Object           privateKey)
        {
            this.privKey = privateKey;
            this.algId = algId;
        }
    
        public PrivateKeyInfo(
            ASN1Sequence  seq)
        {
            IEnumerator e = seq.getObjects();
    
            e.MoveNext();
            BigInteger  version = ((DERInteger)e.Current).getValue();
            if (version.intValue() != 0)
            {
                throw new ArgumentException("wrong version for private key info");
            }
    
            e.MoveNext();
            algId = new AlgorithmIdentifier((ASN1Sequence)e.Current);
    
            try
            {
                e.MoveNext();
                MemoryStream    bIn = new MemoryStream(((ASN1OctetString)e.Current).getOctets());
                ASN1InputStream         aIn = new ASN1InputStream(bIn);
    
                privKey = aIn.readObject();
            }
            catch (IOException)
            {
                throw new ArgumentException("Error recoverying private key from sequence");
            }
        }
    
        public AlgorithmIdentifier getAlgorithmId()
        {
            return algId;
        }
    
        public ASN1Object getPrivateKey()
        {
            return privKey;
        }
    
        /**
         * write out an RSA private key with it's asscociated information
         * as described in PKCS8.
         * <pre>
         *      PrivateKeyInfo ::= SEQUENCE {
         *                              version Version,
         *                              privateKeyAlgorithm AlgorithmIdentifier {{PrivateKeyAlgorithms}},
         *                              privateKey PrivateKey,
         *                              attributes [0] IMPLICIT Attributes OPTIONAL 
         *                          }
         *      Version ::= INTEGER {v1(0)} (v1,...)
         *
         *      PrivateKey ::= OCTET STRING
         *
         *      Attributes ::= SET OF Attribute
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(new DERInteger(0));
            v.add(algId);
            v.add(new DEROctetString(privKey));
    
            return new DERSequence(v);
        }
    }
}
