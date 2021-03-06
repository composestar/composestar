using org.bouncycastle.asn1;
using org.bouncycastle.math;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    /**
     * the infamous Pfx from PKCS12
     */
    public class Pfx
        : ASN1Encodable//, PKCSObjectIdentifiers
    {
        private ContentInfo             contentInfo;
        private MacData                 macData = null;
    
        public Pfx(
            ASN1Sequence   seq)
        {
            BigInteger  version = ((DERInteger)seq.getObjectAt(0)).getValue();
            if (version.intValue() != 3)
            {
                throw new ArgumentException("wrong version for PFX PDU");
            }
    
            contentInfo = ContentInfo.getInstance(seq.getObjectAt(1));
    
            if (seq.size() == 3)
            {
                macData = MacData.getInstance(seq.getObjectAt(2));
            }
        }
    
        public Pfx(
            ContentInfo     contentInfo,
            MacData         macData)
        {
            this.contentInfo = contentInfo;
            this.macData = macData;
        }
    
        public ContentInfo getAuthSafe()
        {
            return contentInfo;
        }
    
        public MacData getMacData()
        {
            return macData;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(new DERInteger(3));
            v.add(contentInfo);
    
            if (macData != null)
            {
                v.add(macData);
            }
    
            return new BERSequence(v);
        }
    }
}
