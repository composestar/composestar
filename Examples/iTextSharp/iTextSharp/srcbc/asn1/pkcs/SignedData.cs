using org.bouncycastle.asn1;

using System;
using System.Collections;

namespace org.bouncycastle.asn1.pkcs
{
    /**
     * a PKCS#7 signed data object.
     */
    public class SignedData
        : ASN1Encodable//, PKCSObjectIdentifiers
    {
        private DERInteger              version;
        private ASN1Set                 digestAlgorithms;
        private ContentInfo             contentInfo;
        private ASN1Set                 certificates;
        private ASN1Set                 crls;
        private ASN1Set                 signerInfos;
    
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
    
            throw new ArgumentException("unknown object in factory: " + o);
        }
    
        public SignedData(
            DERInteger        _version,
            ASN1Set           _digestAlgorithms,
            ContentInfo       _contentInfo,
            ASN1Set           _certificates,
            ASN1Set           _crls,
            ASN1Set           _signerInfos)
        {
            version          = _version;
            digestAlgorithms = _digestAlgorithms;
            contentInfo      = _contentInfo;
            certificates     = _certificates;
            crls             = _crls;
            signerInfos      = _signerInfos;
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
                // an interesting feature of SignedData is that there appear to be varying implementations...
                // for the moment we ignore anything which doesn't fit.
                //
                if (o is DERTaggedObject)
                {
                    DERTaggedObject tagged = (DERTaggedObject)o;
    
                    switch ((int)tagged.getTagNo())
                    {
                    case 0:
                        certificates = ASN1Set.getInstance(tagged, false);
                        break;
                    case 1:
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
    
        public ContentInfo getContentInfo()
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
         *  SignedData ::= SEQUENCE {
         *      version Version,
         *      digestAlgorithms DigestAlgorithmIdentifiers,
         *      contentInfo ContentInfo,
         *      certificates
         *          [0] IMPLICIT ExtendedCertificatesAndCertificates
         *                   OPTIONAL,
         *      crls
         *          [1] IMPLICIT CertificateRevocationLists OPTIONAL,
         *      signerInfos SignerInfos }
         * </pre>
         */
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(digestAlgorithms);
            v.add(contentInfo);
    
            if (certificates != null)
            {
                v.add(new DERTaggedObject(false, 0, certificates));
            }
    
            if (crls != null)
            {
                v.add(new DERTaggedObject(false, 1, crls));
            }
    
            v.add(signerInfos);
    
            return new BERSequence(v);
        }
    }
}
