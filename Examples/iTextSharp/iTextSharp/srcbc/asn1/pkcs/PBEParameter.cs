using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    public class PBEParameter : ASN1Encodable
    {
        ASN1OctetString     octStr;
        DERInteger          iterationCount;

        public static PBEParameter getInstance(
            object o)
        {
            if (o is PBEParameter || o == null)
            {
                return (PBEParameter)o;
            }
            else if (o is ASN1Sequence)
            {
                return new PBEParameter((ASN1Sequence)o);
            }

            throw new ArgumentException("unknown object in factory: " + o);
        }

        public PBEParameter(ASN1Sequence  seq)
        {
            IEnumerator e = seq.getObjects();
    
            e.MoveNext();
            octStr = (ASN1OctetString)e.Current;
            e.MoveNext();
            iterationCount = (DERInteger)e.Current;
        }

        public PBEParameter(
            byte[] salt,
            int iterationCount)
        {
            this.octStr = new DEROctetString(salt);
            this.iterationCount = new DERInteger(iterationCount);
        }

        public byte[] getSalt()
        {
            return octStr.getOctets();
        }
    
        public BigInteger getIterationCount()
        {
            return iterationCount.getValue();
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();

            v.add(octStr);
            v.add(iterationCount);
    
            return new DERSequence(v);
        }
    }
}
