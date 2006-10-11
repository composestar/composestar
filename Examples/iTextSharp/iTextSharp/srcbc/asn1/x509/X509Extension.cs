using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * an object for the elements in the X.509 V3 extension block.
     */
    public class X509Extension
    {
        bool             critical;
        ASN1OctetString      value;
    
        public X509Extension(
            DERBoolean              critical,
            ASN1OctetString         value)
        {
            this.critical = critical.isTrue();
            this.value = value;
        }
    
        public X509Extension(
            bool                 critical,
            ASN1OctetString         value)
        {
            this.critical = critical;
            this.value = value;
        }
    
        public bool isCritical()
        {
            return critical;
        }
    
        public ASN1OctetString getValue()
        {
            return value;
        }
    
        public override int GetHashCode()
        {
            if (this.isCritical())
            {
                return this.getValue().GetHashCode();
            }
    
            
            return ~this.getValue().GetHashCode();
        }
    
        public override bool Equals(
            object  o)
        {
            if (o == null || !(o is X509Extension))
            {
                return false;
            }
    
            X509Extension   other = (X509Extension)o;
    
            return other.getValue().Equals(this.getValue())
                && (other.isCritical() == this.isCritical());
        }
    }
}
