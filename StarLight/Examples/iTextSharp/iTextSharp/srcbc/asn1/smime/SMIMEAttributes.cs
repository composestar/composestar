using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;

namespace org.bouncycastle.asn1.smime
{
    public abstract class SMIMEAttributes
    {
        public static readonly DERObjectIdentifier  smimeCapabilities = PKCSObjectIdentifiers.pkcs_9_at_smimeCapabilities;
        public static readonly DERObjectIdentifier  encrypKeyPref = PKCSObjectIdentifiers.id_aa_encrypKeyPref;
    }
}
