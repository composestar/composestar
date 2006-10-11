namespace org.bouncycastle.asn1.pkcs
{
    public abstract class PKCSObjectIdentifiers
    {
        //
        // pkcs-1 OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 1 }
        //
        const string                 pkcs_1                  = "1.2.840.113549.1.1";
        public static readonly DERObjectIdentifier    rsaEncryption           = new DERObjectIdentifier(pkcs_1 + ".1");
        public static readonly DERObjectIdentifier    md2WithRSAEncryption    = new DERObjectIdentifier(pkcs_1 + ".2");
        public static readonly DERObjectIdentifier    md4WithRSAEncryption    = new DERObjectIdentifier(pkcs_1 + ".3");
        public static readonly DERObjectIdentifier    md5WithRSAEncryption    = new DERObjectIdentifier(pkcs_1 + ".4");
        public static readonly DERObjectIdentifier    sha1WithRSAEncryption   = new DERObjectIdentifier(pkcs_1 + ".5");
        public static readonly DERObjectIdentifier    srsaOAEPEncryptionSET   = new DERObjectIdentifier(pkcs_1 + ".6");
        public static readonly DERObjectIdentifier    sha256WithRSAEncryption   = new DERObjectIdentifier(pkcs_1 + ".11");
        public static readonly DERObjectIdentifier    sha384WithRSAEncryption   = new DERObjectIdentifier(pkcs_1 + ".12");
        public static readonly DERObjectIdentifier    sha512WithRSAEncryption   = new DERObjectIdentifier(pkcs_1 + ".13");
        public static readonly DERObjectIdentifier    sha224WithRSAEncryption   = new DERObjectIdentifier(pkcs_1 + ".14");
    
        //
        // pkcs-3 OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 3 }
        //
        const string                 pkcs_3                  = "1.2.840.113549.1.3";
        public static readonly DERObjectIdentifier    dhKeyAgreement          = new DERObjectIdentifier(pkcs_3 + ".1");
    
        //
        // pkcs-5 OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 5 }
        //
        static readonly string                 pkcs_5                  = "1.2.840.113549.1.5";
    
        public static readonly DERObjectIdentifier    pbeWithMD2AndDES_CBC    = new DERObjectIdentifier(pkcs_5 + ".1");
        public static readonly DERObjectIdentifier    pbeWithMD2AndRC2_CBC    = new DERObjectIdentifier(pkcs_5 + ".4");
        public static readonly DERObjectIdentifier    pbeWithMD5AndDES_CBC    = new DERObjectIdentifier(pkcs_5 + ".3");
        public static readonly DERObjectIdentifier    pbeWithMD5AndRC2_CBC    = new DERObjectIdentifier(pkcs_5 + ".6");
        public static readonly DERObjectIdentifier    pbeWithSHA1AndDES_CBC   = new DERObjectIdentifier(pkcs_5 + ".10");
        public static readonly DERObjectIdentifier    pbeWithSHA1AndRC2_CBC   = new DERObjectIdentifier(pkcs_5 + ".11");

        public static readonly DERObjectIdentifier    id_PBES2                = new DERObjectIdentifier(pkcs_5 + ".13");
    
        public static readonly DERObjectIdentifier    id_PBKDF2               = new DERObjectIdentifier(pkcs_5 + ".12");
    
        //
        // encryptionAlgorithm OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) 3 }
        //
        const string                 encryptionAlgorithm     = "1.2.840.113549.3";
    
        public static readonly DERObjectIdentifier    des_EDE3_CBC            = new DERObjectIdentifier(encryptionAlgorithm + ".7");
        public static readonly DERObjectIdentifier    RC2_CBC                 = new DERObjectIdentifier(encryptionAlgorithm + ".2");
    
        //
        // object identifiers for digests
        //
        const string                 digestAlgorithm     = "1.2.840.113549.2";
    
        //
        // md2 OBJECT IDENTIFIER ::=
        //      {iso(1) member-body(2) US(840) rsadsi(113549) digestAlgorithm(2) 2}
        //
        public static readonly DERObjectIdentifier    md2                     = new DERObjectIdentifier(digestAlgorithm + ".2");
    
        //
        // md4 OBJECT IDENTIFIER ::=
        //      {iso(1) member-body(2) US(840) rsadsi(113549) digestAlgorithm(2) 4}
        //
        public static readonly DERObjectIdentifier    md4                     = new DERObjectIdentifier(digestAlgorithm + ".4");
    
        //
        // md5 OBJECT IDENTIFIER ::=
        //      {iso(1) member-body(2) US(840) rsadsi(113549) digestAlgorithm(2) 5}
        //
        public static readonly DERObjectIdentifier    md5                     = new DERObjectIdentifier(digestAlgorithm + ".5");

        public static readonly DERObjectIdentifier    id_hmacWithSHA1                     = new DERObjectIdentifier(digestAlgorithm + ".7");
    
        //
        // pkcs-7 OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 7 }
        //
        const string                 pkcs_7                  = "1.2.840.113549.1.7";
        public static readonly DERObjectIdentifier    data                    = new DERObjectIdentifier(pkcs_7 + ".1");
        public static readonly DERObjectIdentifier    signedData              = new DERObjectIdentifier(pkcs_7 + ".2");
        public static readonly DERObjectIdentifier    envelopedData           = new DERObjectIdentifier(pkcs_7 + ".3");
        public static readonly DERObjectIdentifier    signedAndEnvelopedData  = new DERObjectIdentifier(pkcs_7 + ".4");
        public static readonly DERObjectIdentifier    digestedData            = new DERObjectIdentifier(pkcs_7 + ".5");
        public static readonly DERObjectIdentifier    encryptedData           = new DERObjectIdentifier(pkcs_7 + ".6");
    
        //
        // pkcs-9 OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 9 }
        //
        const string                 pkcs_9                  = "1.2.840.113549.1.9";
    
        public static readonly DERObjectIdentifier    pkcs_9_at_emailAddress  = new DERObjectIdentifier(pkcs_9 + ".1");
        public static readonly DERObjectIdentifier    pkcs_9_at_unstructuredName = new DERObjectIdentifier(pkcs_9 + ".2");
        public static readonly DERObjectIdentifier    pkcs_9_at_contentType = new DERObjectIdentifier(pkcs_9 + ".3");
        public static readonly DERObjectIdentifier    pkcs_9_at_messageDigest = new DERObjectIdentifier(pkcs_9 + ".4");
        public static readonly DERObjectIdentifier    pkcs_9_at_signingTime = new DERObjectIdentifier(pkcs_9 + ".5");
        public static readonly DERObjectIdentifier    pkcs_9_at_counterSignature = new DERObjectIdentifier(pkcs_9 + ".6");
        public static readonly DERObjectIdentifier    pkcs_9_at_challengePassword = new DERObjectIdentifier(pkcs_9 + ".7");
        public static readonly DERObjectIdentifier    pkcs_9_at_unstructuredAddress = new DERObjectIdentifier(pkcs_9 + ".8");
        public static readonly DERObjectIdentifier    pkcs_9_at_extendedCertificateAttributes = new DERObjectIdentifier(pkcs_9 + ".9");
    
        public static readonly DERObjectIdentifier    pkcs_9_at_signingDescription = new DERObjectIdentifier(pkcs_9 + ".13");
        public static readonly DERObjectIdentifier    pkcs_9_at_extensionRequest = new DERObjectIdentifier(pkcs_9 + ".14");
        public static readonly DERObjectIdentifier    pkcs_9_at_smimeCapabilities = new DERObjectIdentifier(pkcs_9 + ".15");
    
        public static readonly DERObjectIdentifier    pkcs_9_at_friendlyName  = new DERObjectIdentifier(pkcs_9 + ".20");
        public static readonly DERObjectIdentifier    pkcs_9_at_localKeyId    = new DERObjectIdentifier(pkcs_9 + ".21");
    
        public static readonly DERObjectIdentifier    x509certType            = new DERObjectIdentifier(pkcs_9 + ".22.1");
    
        public static readonly DERObjectIdentifier    id_ct_compressedData    = new DERObjectIdentifier(pkcs_9 + ".16.1.9");
        
        public static readonly DERObjectIdentifier    id_alg_PWRI_KEK    = new DERObjectIdentifier(pkcs_9 + ".16.3.9");
    
        //
        // SMIME capability sub oids.
        //
        public static readonly DERObjectIdentifier    preferSignedData        = new DERObjectIdentifier(pkcs_9 + ".15.1");
        public static readonly DERObjectIdentifier    canNotDecryptAny        = new DERObjectIdentifier(pkcs_9 + ".15.2");
        public static readonly DERObjectIdentifier    sMIMECapabilitiesVersions = new DERObjectIdentifier(pkcs_9 + ".15.3");
    
        //
        // other SMIME attributes
        //
    
        //
        // id-aa OBJECT IDENTIFIER ::= {iso(1) member-body(2) usa(840)
        // rsadsi(113549) pkcs(1) pkcs-9(9) smime(16) attributes(2)}
        //
        const string id_aa = "1.2.840.113549.1.9.16.2";
        
        /*
         * id-aa-encrypKeyPref OBJECT IDENTIFIER ::= {id-aa 11}
         * 
         */
        public static DERObjectIdentifier id_aa_encrypKeyPref = new DERObjectIdentifier(id_aa + ".11");
    
        //
        // pkcs-12 OBJECT IDENTIFIER ::= {
        //       iso(1) member-body(2) us(840) rsadsi(113549) pkcs(1) 12 }
        //
        const string                 pkcs_12                  = "1.2.840.113549.1.12";
        const string                 bagtypes                 = pkcs_12 + ".10.1";
    
        public static readonly DERObjectIdentifier    keyBag                  = new DERObjectIdentifier(bagtypes + ".1");
        public static readonly DERObjectIdentifier    pkcs8ShroudedKeyBag     = new DERObjectIdentifier(bagtypes + ".2");
        public static readonly DERObjectIdentifier    certBag                 = new DERObjectIdentifier(bagtypes + ".3");
        public static readonly DERObjectIdentifier    crlBag                  = new DERObjectIdentifier(bagtypes + ".4");
        public static readonly DERObjectIdentifier    secretBag               = new DERObjectIdentifier(bagtypes + ".5");
        public static readonly DERObjectIdentifier    safeContentsBag         = new DERObjectIdentifier(bagtypes + ".6");

        const string                 pkcs_12PbeIds  = pkcs_12 + ".1";
                                                                                
        public static readonly DERObjectIdentifier    pbeWithSHAAnd128BitRC4 = new DERObjectIdentifier(pkcs_12PbeIds + ".1");
        public static readonly DERObjectIdentifier    pbeWithSHAAnd40BitRC4  = new DERObjectIdentifier(pkcs_12PbeIds + ".2");
        public static readonly DERObjectIdentifier    pbeWithSHAAnd3_KeyTripleDES_CBC = new DERObjectIdentifier(pkcs_12PbeIds + ".3");
        public static readonly DERObjectIdentifier    pbeWithSHAAnd2_KeyTripleDES_CBC = new DERObjectIdentifier(pkcs_12PbeIds + ".4");
        public static readonly DERObjectIdentifier    pbeWithSHAAnd128BitRC2_CBC = new DERObjectIdentifier(pkcs_12PbeIds + ".5");
        public static readonly DERObjectIdentifier    pbewithSHAAnd40BitRC2_CBC = new DERObjectIdentifier(pkcs_12PbeIds + ".6");

    }
}
