using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.sec
{
    /**
     * the elliptic curve private key object from SEC 1
     */
    public class ECPrivateKeyStructure
        : ASN1Encodable
    {
        private ASN1Sequence  seq;
    
        public ECPrivateKeyStructure(
            ASN1Sequence  seq)
        {
            this.seq = seq;
        }
    
        public ECPrivateKeyStructure(
            BigInteger  key)
        {
            byte[]  bytes = key.toByteArray();
    
            if (bytes[0] == 0)
            {
                byte[]  tmp = new byte[bytes.Length - 1];
    
                Array.Copy(bytes, 1, tmp, 0, tmp.Length);
                bytes = tmp;
            }
    
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(new DERInteger(1));
            v.add(new DEROctetString(bytes));
    
            seq = new DERSequence(v);
        }
    
        public BigInteger getKey()
        {
            ASN1OctetString  octs = (ASN1OctetString)seq.getObjectAt(1);
    
            BigInteger  k = new BigInteger(1, octs.getOctets());
    
            return k;
        }
    
        public override ASN1Object toASN1Object()
        {
            return seq;
        }
    }
}
