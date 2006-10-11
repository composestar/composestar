using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.cms
{
    /**
     * a signed data object.
     */
    public class SignedData
        : ASN1Encodable
    {
        private DERInteger  version;
        private ASN1Set     digestAlgorithms;
        private ContentInfo contentInfo;
        private ASN1Set     certificates;
        private ASN1Set     crls;
        private ASN1Set     signerInfos;
        private bool        certBer;
        private bool        crlsBer;
    
        public static SignedData getInstance(
            object  o)
        {
            if (o is SignedData)
            {
                return (SignedData)o;
            }
            else if (o is ASN1Sequence)
            {
                return new SignedData((ASN1Sequence)o);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public SignedData(
            ASN1Set     digestAlgorithms,
            ContentInfo contentInfo,
            ASN1Set     certificates,
            ASN1Set     crls,
            ASN1Set     signerInfos)
        {
            if (contentInfo.getContentType().Equals(CMSObjectIdentifiers.data))
            {
                //
                // we should also be looking for attribute certificates here,
                // later.
                //
                IEnumerator e = signerInfos.getObjects();
                bool     v3Found = false;
    
                while (e.MoveNext())
                {
                    SignerInfo  s = SignerInfo.getInstance(e.Current);
    
                    if (s.getVersion().getValue().intValue() == 3)
                    {
                        v3Found = true;
                    }
                }
    
                if (v3Found)
                {
                    this.version = new DERInteger(3);
                }
                else
                {
                    this.version = new DERInteger(1);
                }
            }
            else
            {
                this.version = new DERInteger(3);
            }
    
            this.digestAlgorithms = digestAlgorithms;
            this.contentInfo = contentInfo;
            this.certificates = certificates;
            this.crls = crls;
            this.signerInfos = signerInfos;
        }
        
        public SignedData(
            ASN1Sequence seq)
        {
            IEnumerator     e = seq.getObjects();

            e.MoveNext();
            version = (DERInteger)e.Current;
            e.MoveNext();
            digestAlgorithms = ((ASN1Set)e.Current);
            e.MoveNext();
            contentInfo = ContentInfo.getInstance(e.Current);
    
            while (e.MoveNext())
            {
                ASN1Object o = (ASN1Object)e.Current;
    
                //
                // an interesting feature of SignedData is that there appear
                // to be varying implementations...
                // for the moment we ignore anything which doesn't fit.
                //
                if (o is ASN1TaggedObject)
                {
                    ASN1TaggedObject tagged = (ASN1TaggedObject)o;
    
                    switch ((int)tagged.getTagNo())
                    {
                    case 0:
                        certBer = tagged is BERTaggedObject;
                        certificates = ASN1Set.getInstance(tagged, false);
                        break;
                    case 1:
                        crlsBer = tagged is BERTaggedObject;
                        crls = ASN1Set.getInstance(tagged, false);
                        break;
                    default:
                        throw new ArgumentException("unknown tag value " + tagged.getTagNo());
                    }
                }
                else
                {
                    signerInfos = (ASN1Set)o;
                }
            }
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public ASN1Set getDigestAlgorithms()
        {
            return digestAlgorithms;
        }
    
        public ContentInfo getEncapContentInfo()
        {
            return contentInfo;
        }
    
        public ASN1Set getCertificates()
        {
            return certificates;
        }
    
        public ASN1Set getCRLs()
        {
            return crls;
        }
    
        public ASN1Set getSignerInfos()
        {
            return signerInfos;
        }
    
        /**
         * Produce an object suitable for an ASN1OutputStream.
         * <pre>
         * SignedData ::= SEQUENCE {
         *     version CMSVersion,
         *     digestAlgorithms DigestAlgorithmIdentifiers,
         *     encapContentInfo EncapsulatedContentInfo,
         *     certificates [0] IMPLICIT CertificateSet OPTIONAL,
         *     crls [1] IMPLICIT CertificateRevocationLists OPTIONAL,
         *     signerInfos SignerInfos
         *   }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(digestAlgorithms);
            v.add(contentInfo);
    
            if (certificates != null)
            {
                if (certBer)
                {
                    v.add(new BERTaggedObject(false, 0, certificates));
                }
                else
                {
                    v.add(new DERTaggedObject(false, 0, certificates));
                }
            }
    
            if (crls != null)
            {
                if (crlsBer)
                {
                    v.add(new BERTaggedObject(false, 1, crls));
                }
                else
                {
                    v.add(new DERTaggedObject(false, 1, crls));
                }
            }
    
            v.add(signerInfos);
    
            return new BERSequence(v);
        }
    }
}
