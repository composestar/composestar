using org.bouncycastle.asn1;
using org.bouncycastle.asn1.pkcs;

namespace org.bouncycastle.asn1.cms
{
    public abstract class CMSAttributes
    {
        public static readonly DERObjectIdentifier  contentType = PKCSObjectIdentifiers.pkcs_9_at_contentType;
        public static readonly DERObjectIdentifier  messageDigest = PKCSObjectIdentifiers.pkcs_9_at_messageDigest;
        public static readonly DERObjectIdentifier  signingTime = PKCSObjectIdentifiers.pkcs_9_at_signingTime;
    }
}
