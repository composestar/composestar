using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    public class PKCS12PBEParams
        : ASN1Encodable
    {
        DERInteger      iterations;
        ASN1OctetString iv;
    
        public PKCS12PBEParams(
            byte[]      salt,
            int         iterations)
        {
            this.iv = new DEROctetString(salt);
            this.iterations = new DERInteger(iterations);
        }
    
        public PKCS12PBEParams(
            ASN1Sequence  seq)
        {
            iv = (ASN1OctetString)seq.getObjectAt(0);
            iterations = (DERInteger)seq.getObjectAt(1);
        }
    
        public static PKCS12PBEParams getInstance(
            object  obj)
        {
            if (obj is PKCS12PBEParams)
            {
                return (PKCS12PBEParams)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new PKCS12PBEParams((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public BigInteger getIterations()
        {
            return iterations.getValue();
        }
    
        public byte[] getIV()
        {
            return iv.getOctets();
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(iv);
            v.add(iterations);
    
            return new DERSequence(v);
        }
    }
}
