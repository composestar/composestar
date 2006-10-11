using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    public class PBKDF2Params : ASN1Encodable
    {
        ASN1OctetString     octStr;
        DERInteger          iterationCount;
        DERInteger          keyLength;

        public static PBKDF2Params getInstance(
            object o)
        {
            if (o is PBKDF2Params || o == null)
            {
                return (PBKDF2Params)o;
            }
            else if (o is ASN1Sequence)
            {
                return new PBKDF2Params((ASN1Sequence)o);
            }

            throw new ArgumentException("unknown object in factory: " + o);
        }

        public PBKDF2Params(ASN1Sequence  seq)
        {
            IEnumerator e = seq.getObjects();
    
            e.MoveNext();
            octStr = (ASN1OctetString)e.Current;
            e.MoveNext();
            iterationCount = (DERInteger)e.Current;
    
            if (e.MoveNext())
            {
                keyLength = (DERInteger)e.Current;
            }
            else
            {
                keyLength = null;
            }
        }

        public PBKDF2Params(
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
    
        public BigInteger getKeyLength()
        {
            if (keyLength != null)
            {
                return keyLength.getValue();
            }
    
            return null;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();

            v.add(octStr);
            v.add(iterationCount);
    
            if (keyLength != null)
            {
                v.add(keyLength);
            }
    
            return new DERSequence(v);
        }
    }
}
