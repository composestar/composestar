using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x509
{
    public abstract class X509ObjectIdentifiers
    {
        //
        // base id
        //
        internal const string                 id                      = "2.5.4";
    
        public static readonly DERObjectIdentifier    commonName              = new DERObjectIdentifier(id + ".3");
        public static readonly DERObjectIdentifier    countryName             = new DERObjectIdentifier(id + ".6");
        public static readonly DERObjectIdentifier    localityName            = new DERObjectIdentifier(id + ".7");
        public static readonly DERObjectIdentifier    stateOrProvinceName     = new DERObjectIdentifier(id + ".8");
        public static readonly DERObjectIdentifier    organization            = new DERObjectIdentifier(id + ".10");
        public static readonly DERObjectIdentifier    organizationalUnitName  = new DERObjectIdentifier(id + ".11");
    
        // id-SHA1 OBJECT IDENTIFIER ::=    
        //   {iso(1) identified-organization(3) oiw(14) secsig(3) algorithms(2) 26 }    //
        public static readonly DERObjectIdentifier    id_SHA1                 = new DERObjectIdentifier("1.3.14.3.2.26");
    
        //
        // ripemd160 OBJECT IDENTIFIER ::=
        //      {iso(1) identified-organization(3) TeleTrust(36) algorithm(3) hashAlgorithm(2) RIPEMD-160(1)}
        //
        public static readonly DERObjectIdentifier    ripemd160               = new DERObjectIdentifier("1.3.36.3.2.1");
    
        //
        // ripemd160WithRSAEncryption OBJECT IDENTIFIER ::=
        //      {iso(1) identified-organization(3) TeleTrust(36) algorithm(3) signatureAlgorithm(3) rsaSignature(1) rsaSignatureWithripemd160(2) }
        //
        public static readonly DERObjectIdentifier    ripemd160WithRSAEncryption = new DERObjectIdentifier("1.3.36.3.3.1.2");
    
    
        public static readonly DERObjectIdentifier    id_ea_rsa = new DERObjectIdentifier("2.5.8.1.1");
        
        //
        //    OID for ocsp uri in AuthorityInformationAccess extension
        //
        public static readonly DERObjectIdentifier ocspAccessMethod = new DERObjectIdentifier("1.3.6.1.5.5.7.48.1");
    }
}
