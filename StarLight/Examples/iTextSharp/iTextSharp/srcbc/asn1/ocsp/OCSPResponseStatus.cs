using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.ocsp
{
    public class OCSPResponseStatus
        : DEREnumerated
    {
        public const int SUCCESSFUL = 0;
        public const int MALFORMED_REQUEST = 1;
        public const int INTERNAL_ERROR = 2;
        public const int TRY_LATER = 3;
        public const int SIG_REQUIRED = 5;
        public const int UNAUTHORIZED = 6;
    
        /**
         * The OCSPResponseStatus enumeration.
         * <pre>
         * OCSPResponseStatus ::= ENUMERATED {
         *     successful            (0),  --Response has valid confirmations
         *     malformedRequest      (1),  --Illegal confirmation request
         *     internalError         (2),  --Internal error in issuer
         *     tryLater              (3),  --Try again later
         *                                 --(4) is not used
         *     sigRequired           (5),  --Must sign the request
         *     unauthorized          (6)   --Request unauthorized
         * }
         * </pre>
         */
        public OCSPResponseStatus(int value) : base(value)
        {
        }
    
        public OCSPResponseStatus(DEREnumerated value) : base(value.getValue().intValue())
        {
        }
    }
}
