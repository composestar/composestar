using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.teletrust
{
    public abstract class TeleTrusTObjectIdentifiers
    {
        const string teleTrusTAlgorithm = "1.3.36.3";
    
        public static readonly DERObjectIdentifier    ripemd160           = new DERObjectIdentifier(teleTrusTAlgorithm + ".2.1");
        public static readonly DERObjectIdentifier    ripemd128           = new DERObjectIdentifier(teleTrusTAlgorithm + ".2.2");
        public static readonly DERObjectIdentifier    ripemd256           = new DERObjectIdentifier(teleTrusTAlgorithm + ".2.3");
    
        const string teleTrusTRSAsignatureAlgorithm = teleTrusTAlgorithm + ".3.1";
    
        public static readonly DERObjectIdentifier    rsaSignatureWithripemd160           = new DERObjectIdentifier(teleTrusTRSAsignatureAlgorithm + ".2");
        public static readonly DERObjectIdentifier    rsaSignatureWithripemd128           = new DERObjectIdentifier(teleTrusTRSAsignatureAlgorithm + ".3");
        public static readonly DERObjectIdentifier    rsaSignatureWithripemd256           = new DERObjectIdentifier(teleTrusTRSAsignatureAlgorithm + ".4");
    }
}
