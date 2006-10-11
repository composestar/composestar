using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    /**
     * The KeyPurposeId object.
     * <pre>
     *     KeyPurposeId ::= OBJECT IDENTIFIER
     * </pre>
     */
    public class KeyPurposeId
        : DERObjectIdentifier
    {
        private const string id_kp = "1.3.6.1.5.5.7.3";
    
        private KeyPurposeId(string id) : base(id)
        {
        }
    
        public static readonly KeyPurposeId anyExtendedKeyUsage = new KeyPurposeId(X509Extensions.ExtendedKeyUsage.getId() + ".0");
        public static readonly KeyPurposeId id_kp_serverAuth = new KeyPurposeId(id_kp + ".1");
        public static readonly KeyPurposeId id_kp_clientAuth = new KeyPurposeId(id_kp + ".2");
        public static readonly KeyPurposeId id_kp_codeSigning = new KeyPurposeId(id_kp + ".3");
        public static readonly KeyPurposeId id_kp_emailProtection = new KeyPurposeId(id_kp + ".4");
        public static readonly KeyPurposeId id_kp_ipsecEndSystem = new KeyPurposeId(id_kp + ".5");
        public static readonly KeyPurposeId id_kp_ipsecTunnel = new KeyPurposeId(id_kp + ".6");
        public static readonly KeyPurposeId id_kp_ipsecUser = new KeyPurposeId(id_kp + ".7");
        public static readonly KeyPurposeId id_kp_timeStamping = new KeyPurposeId(id_kp + ".8");
        public static readonly KeyPurposeId id_kp_OCSPSigning = new KeyPurposeId(id_kp + ".9");
    
        //
        // microsoft key purpose ids
        //
        public static readonly KeyPurposeId id_kp_smartcardlogon = new KeyPurposeId("1.3.6.1.4.1.311.20.2.2");
    }
}
