using org.bouncycastle.asn1;

using System;

namespace org.bouncycastle.asn1.misc
{
    public class CAST5CBCParameters
        : ASN1Encodable
    {
        DERInteger      keyLength;
        ASN1OctetString iv;
    
        public static CAST5CBCParameters getInstance(
            object  o)
        {
            if (o is CAST5CBCParameters)
            {
                return (CAST5CBCParameters)o;
            }
            else if (o is ASN1Sequence)
            {
                return new CAST5CBCParameters((ASN1Sequence)o);
            }
    
            throw new ArgumentException("unknown object in CAST5CBCParameter factory");
        }
    
        public CAST5CBCParameters(
            byte[]  iv,
            int     keyLength)
        {
            this.iv = new DEROctetString(iv);
            this.keyLength = new DERInteger(keyLength);
        }
    
        public CAST5CBCParameters(
            ASN1Sequence  seq)
        {
            iv = (ASN1OctetString)seq.getObjectAt(0);
            keyLength = (DERInteger)seq.getObjectAt(1);
        }
    
        public byte[] getIV()
        {
            return iv.getOctets();
        }
    
        public int getKeyLength()
        {
            return keyLength.getValue().intValue();
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * cast5CBCParameters ::= SEQUENCE {
         *                           iv         OCTET STRING DEFAULT 0,
         *                                  -- Initialization vector
         *                           keyLength  INTEGER
         *                                  -- Key length, in bits
         *                      }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(iv);
            v.add(keyLength);
    
            return new DERSequence(v);
        }
    }
}
