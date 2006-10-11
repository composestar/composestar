using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    public class ContentInfo
        : ASN1Encodable//, PKCSObjectIdentifiers
    {
        private DERObjectIdentifier contentType;
        private ASN1Encodable        content;
    
        public static ContentInfo getInstance(
            object  obj)
        {
            if (obj is ContentInfo)
            {
                return (ContentInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new ContentInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public ContentInfo(
            ASN1Sequence  seq)
        {
            IEnumerator   e = seq.getObjects();
    
            e.MoveNext();
            contentType = (DERObjectIdentifier)e.Current;
    
            if (e.MoveNext())
            {
                content = ((DERTaggedObject)e.Current).getObject();
            }
        }
    
        public ContentInfo(
            DERObjectIdentifier contentType,
            ASN1Encodable        content)
        {
            this.contentType = contentType;
            this.content = content;
        }
    
        public DERObjectIdentifier getContentType()
        {
            return contentType;
        }
    
        public ASN1Encodable getContent()
        {
            return content;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * ContentInfo ::= SEQUENCE {
         *          contentType ContentType,
         *          content
         *          [0] EXPLICIT ANY DEFINED BY contentType OPTIONAL }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(contentType);
    
            if (content != null)
            {
                v.add(new BERTaggedObject(0, content));
            }
    
            return new BERSequence(v);
        }
    }
}
