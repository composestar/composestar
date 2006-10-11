using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.ocsp
{
    public abstract class OCSPObjectIdentifiers
    {
        const string pkix_ocsp = "1.3.6.1.5.5.7.48.1";
    
        public static readonly DERObjectIdentifier id_pkix_ocsp = new DERObjectIdentifier(pkix_ocsp);
        public static readonly DERObjectIdentifier id_pkix_ocsp_basic = new DERObjectIdentifier(pkix_ocsp + ".1");
    }
}
