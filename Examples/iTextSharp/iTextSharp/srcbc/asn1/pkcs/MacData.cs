using org.bouncycastle.asn1;
using org.bouncycastle.asn1.x509;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    public class MacData
        : ASN1Encodable
    {
        DigestInfo                  digInfo;
        byte[]                      salt;
        BigInteger                  iterationCount;
    
        public static MacData getInstance(
            object  obj)
        {
            if (obj is MacData)
            {
                return (MacData)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new MacData((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public MacData(
            ASN1Sequence seq)
        {
            this.digInfo = DigestInfo.getInstance(seq.getObjectAt(0));
    
            this.salt = ((ASN1OctetString)seq.getObjectAt(1)).getOctets();
    
            if (seq.size() == 3)
            {
                this.iterationCount = ((DERInteger)seq.getObjectAt(2)).getValue();
            }
            else
            {
                this.iterationCount = BigInteger.valueOf(1);
            }
        }
    
        public MacData(
            DigestInfo  digInfo,
            byte[]      salt,
            int         iterationCount)
        {
            this.digInfo = digInfo;
            this.salt = salt;
            this.iterationCount = BigInteger.valueOf(iterationCount);
        }
    
        public DigestInfo getMac()
        {
            return digInfo;
        }
    
        public byte[] getSalt()
        {
            return salt;
        }
    
        public BigInteger getIterationCount()
        {
            return iterationCount;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(digInfo);
            v.add(new DEROctetString(salt));
            v.add(new DERInteger(iterationCount));
    
            return new DERSequence(v);
        }
    }
}
