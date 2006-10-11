using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.nist
{
    public abstract class NISTObjectIdentifiers
    {
        //
        // NIST
        //     iso/itu(2) joint-assign(16) us(840) organization(1) gov(101) csor(3) 
    
        //
        // nistalgorithms(4)
        //
        const string                 nistAlgorithm          = "2.16.840.1.101.3.4";
    
        public static readonly DERObjectIdentifier    id_sha256               = new DERObjectIdentifier(nistAlgorithm + ".2.1");
        public static readonly DERObjectIdentifier    id_sha384               = new DERObjectIdentifier(nistAlgorithm + ".2.2");
        public static readonly DERObjectIdentifier    id_sha512               = new DERObjectIdentifier(nistAlgorithm + ".2.3");
        public static readonly DERObjectIdentifier    id_sha224               = new DERObjectIdentifier(nistAlgorithm + ".2.4");
    }
}
