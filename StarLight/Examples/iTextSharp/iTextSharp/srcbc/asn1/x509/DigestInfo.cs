using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The DigestInfo object.
     * <pre>
     * DigestInfo::=SEQUENCE{
     *          digestAlgorithm  AlgorithmIdentifier,
     *          digest OCTET STRING }
     * </pre>
     */
    public class DigestInfo
        : ASN1Encodable
    {
        private byte[]                  digest;
        private AlgorithmIdentifier     algId;
    
        public static DigestInfo getInstance(
            ASN1TaggedObject obj,
            bool          explicitly)
        {
            return getInstance(ASN1Sequence.getInstance(obj, explicitly));
        }
    
        public static DigestInfo getInstance(
            object  obj)
        {
            if (obj is DigestInfo)
            {
                return (DigestInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new DigestInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public DigestInfo(
            AlgorithmIdentifier  algId,
            byte[]               digest)
        {
            this.digest = digest;
            this.algId = algId;
        }
    
        public DigestInfo(
            ASN1Sequence  obj)
        {
            IEnumerator             e = obj.getObjects();
            e.MoveNext();
            algId = AlgorithmIdentifier.getInstance(e.Current);
            e.MoveNext();
            digest = ((ASN1OctetString)e.Current).getOctets();
        }
    
        public AlgorithmIdentifier getAlgorithmId()
        {
            return algId;
        }
    
        public byte[] getDigest()
        {
            return digest;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(algId);
            v.add(new DEROctetString(digest));
    
            return new DERSequence(v);
        }
    }
}
