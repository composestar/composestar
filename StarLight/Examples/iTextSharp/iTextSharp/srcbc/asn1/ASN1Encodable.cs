using System;
using System.IO;

namespace org.bouncycastle.asn1
{
    public abstract class ASN1Encodable
    {
        public byte[] getEncoded() 
        {
            MemoryStream bOut = new MemoryStream();
            ASN1OutputStream        aOut = new ASN1OutputStream(bOut);
            
            aOut.writeObject(this);
            
            return bOut.ToArray();
        }
        
        public override int GetHashCode()
        {
            return this.toASN1Object().GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if ((o == null) || !(o is ASN1Encodable))
            {
                return false;
            }
    
            ASN1Encodable other = (ASN1Encodable)o;
    
            return this.toASN1Object().Equals(other.toASN1Object());
        }

        public abstract ASN1Object toASN1Object();
    }
}
