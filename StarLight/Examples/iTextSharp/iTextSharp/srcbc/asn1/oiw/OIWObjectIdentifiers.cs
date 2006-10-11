using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.oiw
{
    public abstract class OIWObjectIdentifiers
    {
        // id-SHA1 OBJECT IDENTIFIER ::=    
        //   {iso(1) identified-organization(3) oiw(14) secsig(3) algorithms(2) 26 }    //
        public static readonly DERObjectIdentifier    id_SHA1                  = new DERObjectIdentifier("1.3.14.3.2.26");
    
        public static readonly DERObjectIdentifier    dsaWithSHA1             = new DERObjectIdentifier("1.3.14.3.2.27");
    
        // ElGamal Algorithm OBJECT IDENTIFIER ::=    
        // {iso(1) identified-organization(3) oiw(14) dirservsig(7) algorithm(2) encryption(1) 1 }
        //
        public static readonly DERObjectIdentifier    elGamalAlgorithm        = new DERObjectIdentifier("1.3.14.7.2.1.1");
    
    }
}
