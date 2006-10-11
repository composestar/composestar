using System;

namespace org.bouncycastle.asn1
{
    /**
     * A NULL object.
     */
    public abstract class ASN1Null
        : ASN1Object
    {
        protected ASN1Null()
        {
        }
    
        public override int GetHashCode()
        {
            return 0;
        }
    
        public override bool Equals(
            object o)
        {
            if ((o == null) || !(o is ASN1Null))
            {
                return false;
            }
            
            return true;
        }
    }
}
