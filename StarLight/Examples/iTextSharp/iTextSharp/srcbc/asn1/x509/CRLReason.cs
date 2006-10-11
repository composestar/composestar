using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The CRLReason enumeration.
     * <pre>
     * CRLReason ::= ENUMERATED {
     *  unspecified             (0),
     *  keyCompromise           (1),
     *  cACompromise            (2),
     *  affiliationChanged      (3),
     *  superseded              (4),
     *  cessationOfOperation    (5),
     *  certificateHold         (6),
     *  removeFromCRL           (8),
     *  privilegeWithdrawn      (9),
     *  aACompromise           (10)
     * }
     * </pre>
     */
    public class CRLReason
        : DEREnumerated
    {
        public const int UNSPECIFIED = 0;
        public const int KEY_COMPROMISE = 1;
        public const int CA_COMPROMISE = 2;
        public const int AFFILIATION_CHANGED = 3;
        public const int SUPERSEDED = 4;
        public const int CESSATION_OF_OPERATION  = 5;
        public const int CERTIFICATE_HOLD = 6;
        public const int REMOVE_FROM_CRL = 8;
        public const int PRIVILEGE_WITHDRAWN = 9;
        public const int AA_COMPROMISE = 10;
    
        public CRLReason(int reason): base(reason)
        {
        }
    
        public CRLReason(DEREnumerated reason) : base(reason.getValue().intValue())
        {
        }
    }
}
