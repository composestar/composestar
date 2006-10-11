using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The ReasonFlags object.
     * <pre>
     * ReasonFlags ::= BIT STRING {
     *    unused(0),
     *    keyCompromise(1),
     *    cACompromise(2),
     *    affiliationChanged(3),
     *    superseded(4),
     *    cessationOfOperation(5),
     *    certficateHold(6)
     * }
     * </pre>
     */
    public class ReasonFlags
        : DERBitString
    {
        public const int UNUSED                  = (1 << 7);
        public const int KEY_COMPROMISE          = (1 << 6);
        public const int CA_COMPROMISE           = (1 << 5);
        public const int AFFILIATION_CHANGED     = (1 << 4);
        public const int SUPERSEDED              = (1 << 3);
        public const int CESSATION_OF_OPERATION  = (1 << 2);
        public const int CERTIFICATE_HOLD        = (1 << 1);
        public const int PRIVILEGE_WITHDRAWN     = (1 << 0);
        public const int AA_COMPROMISE           = (1 << 15);
    
        /**
         * @param reasons - the bitwise OR of the Key Reason flags giving the
         * allowed uses for the key.
         */
        public ReasonFlags(
            int reasons)
             : base(getBytes(reasons), getPadBits(reasons))
        {
        }
    
        public ReasonFlags(
            DERBitString reasons)
             : base(reasons.getBytes(), reasons.getPadBits())
        {
        }
    }
}
