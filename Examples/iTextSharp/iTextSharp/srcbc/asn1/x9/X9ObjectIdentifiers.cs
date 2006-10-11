using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.x9
{
    public abstract class X9ObjectIdentifiers
    {
        //
        // X9.62
        //
        // ansi-X9-62 OBJECT IDENTIFIER ::= { iso(1) member-body(2)
        //            us(840) ansi-x962(10045) }
        //
        internal const string    ansi_X9_62 = "1.2.840.10045";
        internal const string    id_fieldType = ansi_X9_62 + ".1";
    
        public static readonly DERObjectIdentifier    prime_field
                        = new DERObjectIdentifier(id_fieldType + ".1");
    
        public static readonly DERObjectIdentifier    characteristic_two_field
                        = new DERObjectIdentifier(id_fieldType + ".2");
    
        public static readonly DERObjectIdentifier    gnBasis
                        = new DERObjectIdentifier(id_fieldType + ".2.3.1");
    
        public static readonly DERObjectIdentifier    tpBasis
                        = new DERObjectIdentifier(id_fieldType + ".2.3.2");
    
        public static readonly DERObjectIdentifier    ppBasis
                        = new DERObjectIdentifier(id_fieldType + ".2.3.3");
    
        public const string    id_ecSigType = ansi_X9_62 + ".4";
    
        public static readonly DERObjectIdentifier    ecdsa_with_SHA1
                        = new DERObjectIdentifier(id_ecSigType + ".1");
    
        public const string    id_publicKeyType = ansi_X9_62 + ".2";
    
        public static readonly DERObjectIdentifier    id_ecPublicKey
                        = new DERObjectIdentifier(id_publicKeyType + ".1");
    
        //
        // named curves
        //
        internal const string     ellipticCurve = ansi_X9_62 + ".3";
    
        //
        // Two Curves
        //
        internal const string     cTwoCurve = ellipticCurve + ".0";
        
        public static readonly DERObjectIdentifier    c2pnb163v1 = new DERObjectIdentifier(cTwoCurve + ".1");
        public static readonly DERObjectIdentifier    c2pnb163v2 = new DERObjectIdentifier(cTwoCurve + ".2");
        public static readonly DERObjectIdentifier    c2pnb163v3 = new DERObjectIdentifier(cTwoCurve + ".3");
        public static readonly DERObjectIdentifier    c2pnb176w1 = new DERObjectIdentifier(cTwoCurve + ".4");
        public static readonly DERObjectIdentifier    c2tnb191v1 = new DERObjectIdentifier(cTwoCurve + ".5");
        public static readonly DERObjectIdentifier    c2tnb191v2 = new DERObjectIdentifier(cTwoCurve + ".6");
        public static readonly DERObjectIdentifier    c2tnb191v3 = new DERObjectIdentifier(cTwoCurve + ".7");
        public static readonly DERObjectIdentifier    c2onb191v4 = new DERObjectIdentifier(cTwoCurve + ".8");
        public static readonly DERObjectIdentifier    c2onb191v5 = new DERObjectIdentifier(cTwoCurve + ".9");
        public static readonly DERObjectIdentifier    c2pnb208w1 = new DERObjectIdentifier(cTwoCurve + ".10");
        public static readonly DERObjectIdentifier    c2tnb239v1 = new DERObjectIdentifier(cTwoCurve + ".11");
        public static readonly DERObjectIdentifier    c2tnb239v2 = new DERObjectIdentifier(cTwoCurve + ".12");
        public static readonly DERObjectIdentifier    c2tnb239v3 = new DERObjectIdentifier(cTwoCurve + ".13");
        public static readonly DERObjectIdentifier    c2onb239v4 = new DERObjectIdentifier(cTwoCurve + ".14");
        public static readonly DERObjectIdentifier    c2onb239v5 = new DERObjectIdentifier(cTwoCurve + ".15");
        public static readonly DERObjectIdentifier    c2pnb272w1 = new DERObjectIdentifier(cTwoCurve + ".16");
        public static readonly DERObjectIdentifier    c2png304v1 = new DERObjectIdentifier(cTwoCurve + ".17");
        public static readonly DERObjectIdentifier    c2tnb359v1 = new DERObjectIdentifier(cTwoCurve + ".18");
        public static readonly DERObjectIdentifier    c2pnb368w1 = new DERObjectIdentifier(cTwoCurve + ".19");
        public static readonly DERObjectIdentifier    c2tnb431r1 = new DERObjectIdentifier(cTwoCurve + ".20");
        
        //
        // Prime
        //
        internal const string     primeCurve = ellipticCurve + ".1";
    
        public static readonly DERObjectIdentifier    prime192v1 = new DERObjectIdentifier(primeCurve + ".1");
        public static readonly DERObjectIdentifier    prime192v2 = new DERObjectIdentifier(primeCurve + ".2");
        public static readonly DERObjectIdentifier    prime192v3 = new DERObjectIdentifier(primeCurve + ".3");
        public static readonly DERObjectIdentifier    prime239v1 = new DERObjectIdentifier(primeCurve + ".4");
        public static readonly DERObjectIdentifier    prime239v2 = new DERObjectIdentifier(primeCurve + ".5");
        public static readonly DERObjectIdentifier    prime239v3 = new DERObjectIdentifier(primeCurve + ".6");
        public static readonly DERObjectIdentifier    prime256v1 = new DERObjectIdentifier(primeCurve + ".7");
    
        //
        // Diffie-Hellman
        //
        // dhpublicnumber OBJECT IDENTIFIER ::= { iso(1) member-body(2)
        //            us(840) ansi-x942(10046) number-type(2) 1 }
        //
        public static readonly DERObjectIdentifier    dhpublicnumber = new DERObjectIdentifier("1.2.840.10046.2.1");
    
        //
        // DSA
        //
        // dsapublicnumber OBJECT IDENTIFIER ::= { iso(1) member-body(2)
        //            us(840) ansi-x957(10040) number-type(4) 1 }
        public static readonly DERObjectIdentifier    id_dsa = new DERObjectIdentifier("1.2.840.10040.4.1");
    
        /**
         *   id-dsa-with-sha1 OBJECT IDENTIFIER ::=  { iso(1) member-body(2)
         *         us(840) x9-57 (10040) x9cm(4) 3 }
         */
        public static readonly DERObjectIdentifier id_dsa_with_sha1 = new DERObjectIdentifier("1.2.840.10040.4.3");
    }
    
}
