using System;
using System.Collections;
using System.IO;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The object that contains the public key stored in a certficate.
     * <p>
     * The getEncoded() method in the public keys in the JCE produces a DER
     * encoded one of these.
     */
    public class SubjectPublicKeyInfo
        : ASN1Encodable
    {
        private AlgorithmIdentifier     algId;
        private DERBitString            keyData;
    
        public static SubjectPublicKeyInfo getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static SubjectPublicKeyInfo getInstance(
            object  obj)
        {
            if (obj is SubjectPublicKeyInfo)
            {
                return (SubjectPublicKeyInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new SubjectPublicKeyInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public SubjectPublicKeyInfo(
            AlgorithmIdentifier algId,
            ASN1Encodable        publicKey)
        {
            this.keyData = new DERBitString(publicKey);
            this.algId = algId;
        }
    
        public SubjectPublicKeyInfo(
            AlgorithmIdentifier algId,
            byte[]              publicKey)
        {
            this.keyData = new DERBitString(publicKey);
            this.algId = algId;
        }
    
        public SubjectPublicKeyInfo(
            ASN1Sequence  seq)
        {
            IEnumerator         e = seq.getObjects();
            e.MoveNext();
            this.algId = AlgorithmIdentifier.getInstance(e.Current);
            e.MoveNext();
            this.keyData = (DERBitString)e.Current;
        }
    
        public AlgorithmIdentifier getAlgorithmId()
        {
            return algId;
        }
    
        /**
         * for when the public key is an encoded object - if the bitstring
         * can't be decoded this routine throws an IOException.
         *
         * @exception IOException - if the bit string doesn't represent a DER
         * encoded object.
         */
        public ASN1Object getPublicKey()
        {
            MemoryStream    bIn = new MemoryStream(keyData.getBytes());
            ASN1InputStream          dIn = new ASN1InputStream(bIn);
    
            return dIn.readObject();
        }
    
        /**
         * for when the public key is raw bits...
         */
        public DERBitString getPublicKeyData()
        {
            return keyData;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * SubjectPublicKeyInfo ::= SEQUENCE {
         *                          algorithm AlgorithmIdentifier,
         *                          publicKey BIT STRING }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(algId);
            v.add(keyData);
    
            return new DERSequence(v);
        }
    }
}
