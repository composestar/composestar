using org.bouncycastle.asn1.x509;

using System;

namespace org.bouncycastle.asn1.pkcs
{
    /**
     * PKCS10 CertificationRequestInfo object.
     * <pre>
     *  CertificationRequestInfo ::= SEQUENCE {
     *   version             INTEGER { v1(0) } (v1,...),
     *   subject             Name,
     *   subjectPKInfo   SubjectPublicKeyInfo{{ PKInfoAlgorithms }},
     *   attributes          [0] Attributes{{ CRIAttributes }}
     *  }
     *
     *  Attributes { ATTRIBUTE:IOSet } ::= SET OF Attribute{{ IOSet }}
     *
     *  Attribute { ATTRIBUTE:IOSet } ::= SEQUENCE {
     *    type    ATTRIBUTE.&id({IOSet}),
     *    values  SET SIZE(1..MAX) OF ATTRIBUTE.&Type({IOSet}{\@type})
     *  }
     * </pre>
     */
    public class CertificationRequestInfo
        : ASN1Encodable
    {
        DERInteger              version = new DERInteger(0);
        X509Name                subject;
        SubjectPublicKeyInfo    subjectPKInfo;
        ASN1Set                 attributes = null;
    
        public static CertificationRequestInfo getInstance(
            object  obj)
        {
            if (obj is CertificationRequestInfo)
            {
                return (CertificationRequestInfo)obj;
            }
            else if (obj is ASN1Sequence)
            {
                return new CertificationRequestInfo((ASN1Sequence)obj);
            }
    
            throw new ArgumentException("unknown object in factory");
        }
    
        public CertificationRequestInfo(
            X509Name                subject,
            SubjectPublicKeyInfo    pkInfo,
            ASN1Set                 attributes)
        {
            this.subject = subject;
            this.subjectPKInfo = pkInfo;
            this.attributes = attributes;
    
            if ((subject == null) || (version == null) || (subjectPKInfo == null))
            {
                throw new ArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
            }
        }
    
        public CertificationRequestInfo(
            ASN1Sequence  seq)
        {
            version = (DERInteger)seq.getObjectAt(0);
    
            subject = X509Name.getInstance(seq.getObjectAt(1));
            subjectPKInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(2));
    
            //
            // some CertificationRequestInfo objects seem to treat this field
            // as optional.
            //
            if (seq.size() > 3)
            {
                DERTaggedObject tagobj = (DERTaggedObject)seq.getObjectAt(3);
                attributes = ASN1Set.getInstance(tagobj, false);
            }
    
            if ((subject == null) || (version == null) || (subjectPKInfo == null))
            {
                throw new ArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
            }
        }
    
        public DERInteger getVersion()
        {
            return version;
        }
    
        public X509Name getSubject()
        {
            return subject;
        }
    
        public SubjectPublicKeyInfo getSubjectPublicKeyInfo()
        {
            return subjectPKInfo;
        }
    
        public ASN1Set getAttributes()
        {
            return attributes;
        }
    
        public override ASN1Object toASN1Object()
        {
            ASN1EncodableVector  v = new ASN1EncodableVector();
    
            v.add(version);
            v.add(subject);
            v.add(subjectPKInfo);
    
            if (attributes != null)
            {
                v.add(new DERTaggedObject(false, 0, attributes));
            }
    
            return new DERSequence(v);
        }
    }
}
