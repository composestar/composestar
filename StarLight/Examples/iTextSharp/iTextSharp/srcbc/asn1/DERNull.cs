using System;

namespace org.bouncycastle.asn1
{
    /**
     * A NULL object.
     */
    public class DERNull
        : ASN1Null
    {
        byte[]  zeroBytes = new byte[0];
    
        public DERNull()
        {
        }
    
        internal override void encode(
            DEROutputStream  derOut)
        {
            derOut.writeEncoded(ASN1Tags.NULL, zeroBytes);
        }
        
        public override bool Equals(
            object o)
        {
            if ((o == null) || !(o is DERNull))
            {
                return false;
            }
            
            return true;
        }
        
        public override int GetHashCode()
        {
            return 0;
        }
    }
}
