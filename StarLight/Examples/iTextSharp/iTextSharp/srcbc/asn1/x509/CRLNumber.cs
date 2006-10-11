using org.bouncycastle.asn1;
using org.bouncycastle.math;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The CRLNumber object.
     * <pre>
     * CRLNumber::= INTEGER(0..MAX)
     * </pre>
     */
    public class CRLNumber
        : DERInteger
    {
    
        public CRLNumber(BigInteger number) : base(number)
        {
        }
    
        public BigInteger getCRLNumber()
        {
            return getPositiveValue();
        }
    }
}
