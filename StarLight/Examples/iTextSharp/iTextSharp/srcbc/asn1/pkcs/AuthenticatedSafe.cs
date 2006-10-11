using org.bouncycastle.asn1;

namespace org.bouncycastle.asn1.pkcs
{
    public class AuthenticatedSafe
        : ASN1Encodable
    {
        ContentInfo[]    info;
    
        public AuthenticatedSafe(
            ASN1Sequence  seq)
        {
            info = new ContentInfo[seq.size()];
    
            for (int i = 0; i != info.Length; i++)
            {
                info[i] = ContentInfo.getInstance(seq.getObjectAt(i));
            }
        }
    
        public AuthenticatedSafe(
            ContentInfo[]       info)
        {
            this.info = info;
        }
    
        public ContentInfo[] getContentInfo()
        {
            return info;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            for (int i = 0; i != info.Length; i++)
            {
                v.add(info[i]);
            }
    
            return new BERSequence(v);
        }
    }
}
