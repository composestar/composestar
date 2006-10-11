using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.misc
{
    public abstract class MiscObjectIdentifiers
    {
        //
        // Netscape
        //       iso/itu(2) joint-assign(16) us(840) uscompany(1) netscape(113730) cert-extensions(1) }
        //
        internal const string                 netscape                = "2.16.840.1.113730.1";
        public static readonly DERObjectIdentifier    netscapeCertType        = new DERObjectIdentifier(netscape + ".1");
        public static readonly DERObjectIdentifier    netscapeBaseURL         = new DERObjectIdentifier(netscape + ".2");
        public static readonly DERObjectIdentifier    netscapeRevocationURL   = new DERObjectIdentifier(netscape + ".3");
        public static readonly DERObjectIdentifier    netscapeCARevocationURL = new DERObjectIdentifier(netscape + ".4");
        public static readonly DERObjectIdentifier    netscapeRenewalURL      = new DERObjectIdentifier(netscape + ".7");
        public static readonly DERObjectIdentifier    netscapeCApolicyURL     = new DERObjectIdentifier(netscape + ".8");
        public static readonly DERObjectIdentifier    netscapeSSLServerName   = new DERObjectIdentifier(netscape + ".12");
        public static readonly DERObjectIdentifier    netscapeCertComment     = new DERObjectIdentifier(netscape + ".13");
        //
        // Verisign
        //       iso/itu(2) joint-assign(16) us(840) uscompany(1) verisign(113733) cert-extensions(1) }
        //
        internal const string                 verisign                = "2.16.840.1.113733.1";
    
        //
        // CZAG - country, zip, age, and gender
        //
        public static readonly DERObjectIdentifier    verisignCzagExtension   = new DERObjectIdentifier(verisign + ".6.3");
    }
}
