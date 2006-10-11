using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;

namespace org.bouncycastle.asn1.cms
{
    public abstract class CMSObjectIdentifiers
    {
        public static readonly DERObjectIdentifier    data = PKCSObjectIdentifiers.data;
        public static readonly DERObjectIdentifier    signedData = PKCSObjectIdentifiers.signedData;
        public static readonly DERObjectIdentifier    envelopedData = PKCSObjectIdentifiers.envelopedData;
        public static readonly DERObjectIdentifier    signedAndEnvelopedData = PKCSObjectIdentifiers.signedAndEnvelopedData;
        public static readonly DERObjectIdentifier    digestedData = PKCSObjectIdentifiers.digestedData;
        public static readonly DERObjectIdentifier    encryptedData = PKCSObjectIdentifiers.encryptedData;
        public static readonly DERObjectIdentifier    compressedData = PKCSObjectIdentifiers.id_ct_compressedData;
    }
}
